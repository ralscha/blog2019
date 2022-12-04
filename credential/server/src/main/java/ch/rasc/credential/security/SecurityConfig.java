package ch.rasc.credential.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
  // @formatter:off
	  http
		.csrf().disable()
	  .cors()
  	  .and()
  	.formLogin()  	
  	  .successHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK))
  	  .failureHandler((request, response, exception) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
  	  .permitAll()
  	  .and()
  	.logout()
  	  .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
  	  .deleteCookies("JSESSIONID")
  	  .and()
		.authorizeHttpRequests().anyRequest().authenticated()
      .and()
    .exceptionHandling()
      .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
	  return http.build();
    // @formatter:on
  }

  @Bean
  public InMemoryUserDetailsManager userDetailsService() {
    UserDetails adminUser = User.withDefaultPasswordEncoder().username("admin")
        .password("admin").roles("ADMIN").build();
    UserDetails userUser = User.withDefaultPasswordEncoder().username("user")
        .password("user").roles("USER").build();
    return new InMemoryUserDetailsManager(adminUser, userUser);
  }

  @SuppressWarnings("null")
  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration
        .setAllowedOrigins(List.of("http://localhost:8100", "http://localhost:1234"));
    configuration.setAllowedMethods(List.of("GET", "POST"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}