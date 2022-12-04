package ch.rasc.stateless.config.security;

import static ch.rasc.stateless.db.tables.AppSession.APP_SESSION;

import java.io.IOException;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  private final AuthCookieFilter authCookieFilter;

  private final CustomLogoutSuccessHandler logoutSuccessHandler;

  private final SecurityContextRepository securityContextRepository;

  public SecurityConfig(DSLContext dsl,
      SecurityContextRepository securityContextRepository) {
    this.securityContextRepository = securityContextRepository;
    this.authCookieFilter = new AuthCookieFilter(dsl, securityContextRepository);
    this.logoutSuccessHandler = new CustomLogoutSuccessHandler(dsl);
  }

  @Bean
  AuthenticationManager authenticationManager() {
    return authentication -> {
      throw new AuthenticationServiceException("Cannot authenticate " + authentication);
    };
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.sessionManagement(
        cust -> cust.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .securityContext(securityContext -> securityContext
            .securityContextRepository(this.securityContextRepository))
        .headers(cust -> cust.contentSecurityPolicy(
            "script-src 'self'; object-src 'none'; base-uri 'self'"))
        .csrf(CsrfConfigurer::disable).logout(cust -> {
          cust.addLogoutHandler(new HeaderWriterLogoutHandler(
              new ClearSiteDataHeaderWriter(Directive.ALL)));
          cust.logoutSuccessHandler(this.logoutSuccessHandler);
          cust.deleteCookies(AuthCookieFilter.COOKIE_NAME);
        }).authorizeHttpRequests(cust -> {
          cust.requestMatchers("/login").permitAll().anyRequest().authenticated();
        })
        .exceptionHandling(cust -> cust
            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .addFilterAfter(this.authCookieFilter, SecurityContextHolderFilter.class);
    return http.build();
  }

  private static class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final DSLContext dsl;

    public CustomLogoutSuccessHandler(DSLContext dsl) {
      this.dsl = dsl;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {

      String sessionId = AuthCookieFilter.extractAuthenticationCookie(request);
      if (sessionId != null) {
        this.dsl.delete(APP_SESSION).where(APP_SESSION.ID.eq(sessionId)).execute();
      }

      response.setStatus(HttpServletResponse.SC_OK);
      response.getWriter().flush();
    }

  }

}
