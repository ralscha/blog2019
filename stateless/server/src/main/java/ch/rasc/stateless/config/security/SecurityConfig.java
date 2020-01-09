package ch.rasc.stateless.config.security;

import static ch.rasc.stateless.db.tables.AppSession.APP_SESSION;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final AuthCookieFilter authCookieFilter;

  private final CustomLogoutSuccessHandler logoutSuccessHandler;

  public SecurityConfig(DSLContext dsl) {
    this.authCookieFilter = new AuthCookieFilter(dsl);
    this.logoutSuccessHandler = new CustomLogoutSuccessHandler(dsl);
  }

  @Bean
  @Override
  protected AuthenticationManager authenticationManager() throws Exception {
    return authentication -> {
      throw new AuthenticationServiceException("Cannot authenticate " + authentication);
    };
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    String defaultEncodingId = "argon2";
    Map<String, PasswordEncoder> encoders = new HashMap<>();
    encoders.put(defaultEncodingId, new Argon2PasswordEncoder(16, 32, 8, 1 << 16, 4));
    return new DelegatingPasswordEncoder(defaultEncodingId, encoders);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .sessionManagement(cust -> cust.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .headers(cust -> cust.contentSecurityPolicy(
            "script-src 'self'; object-src 'none'; base-uri 'self'"))
        .csrf(cust -> cust.disable())
        .logout(cust -> {
          cust.addLogoutHandler(new HeaderWriterLogoutHandler(
              new ClearSiteDataHeaderWriter(Directive.ALL)));
          cust.logoutSuccessHandler(this.logoutSuccessHandler);
          cust.deleteCookies(AuthCookieFilter.COOKIE_NAME);
        })
        .authorizeRequests(cust -> {
          cust.antMatchers("/login").permitAll().anyRequest().authenticated();
        })
        .exceptionHandling(cust -> cust
            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .addFilterAfter(this.authCookieFilter, SecurityContextPersistenceFilter.class);
  }

  private class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

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
