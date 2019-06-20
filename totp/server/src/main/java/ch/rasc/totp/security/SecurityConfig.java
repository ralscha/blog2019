package ch.rasc.totp.security;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.context.request.WebRequest;

import com.codahale.passpol.BreachDatabase;
import com.codahale.passpol.PasswordPolicy;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private TotpWebAuthenticationDetailsSource totpWebAuthenticationDetailsSource;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // @formatter:off
       http
	.csrf().disable()
  	.formLogin()
  	  .successHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK))
  	  .failureHandler((request, response, exception) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
  	  .authenticationDetailsSource(this.totpWebAuthenticationDetailsSource)
  	  .permitAll()
  	.and()
  	  .logout()
  	    .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
  	    .deleteCookies("JSESSIONID")
  	.and()
  	  .authorizeRequests()
  	    .antMatchers("/signup", "/signup-confirm-secret").permitAll()
  	    .anyRequest().authenticated()
        .and()
          .exceptionHandling()
            .authenticationEntryPoint((request, response, exception) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED));
    // @formatter:on
  }

  @Configuration
  @Order(1)
  public static class H2ConsoleSecurityConfigurationAdapter
      extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
      //@formatter:off
			http
			  .antMatcher("/h2-console/**")
			    .authorizeRequests()
			      .anyRequest().permitAll()//.fullyAuthenticated()
			  .and()
			    .csrf().disable()
			    .headers().frameOptions().sameOrigin();
			//@formatter:on
    }
  }

  @Bean
  public ErrorAttributes errorAttributes() {
    return new DefaultErrorAttributes() {
      @Override
      public Map<String, Object> getErrorAttributes(WebRequest webRequest,
          boolean includeStackTrace) {
        return Map.of();
      }
    };
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public PasswordPolicy passwordPolicy() {
    return new PasswordPolicy(BreachDatabase.top100K(), 8, 256);
  }

}
