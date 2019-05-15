package ch.rasc.stateless.config.security;

import static ch.rasc.stateless.db.tables.AppUser.APP_USER;

import java.io.IOException;
import java.time.Instant;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.jooq.DSLContext;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import ch.rasc.stateless.db.tables.records.AppUserRecord;

@Component
public class AuthCookieFilter extends GenericFilterBean {

  public final static String COOKIE_NAME = "X-authentication";

  private final DSLContext dsl;

  private final CryptoService cryptoService;

  private final UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();

  public AuthCookieFilter(DSLContext dsl, CryptoService cryptoService) {
    this.dsl = dsl;
    this.cryptoService = cryptoService;
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

    Cookie[] cookies = httpServletRequest.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(COOKIE_NAME)) {
          String decryptedCookieValue = this.cryptoService.decrypt(cookie.getValue());
          if (decryptedCookieValue != null) {
            int colonPos = decryptedCookieValue.indexOf(':');
            String appUserIdString = decryptedCookieValue.substring(0, colonPos);
            long expiresAtEpochSeconds = Long
                .valueOf(decryptedCookieValue.substring(colonPos + 1));

            if (Instant.now().getEpochSecond() < expiresAtEpochSeconds) {
              try {
                AppUserRecord appUserRecord = this.dsl.selectFrom(APP_USER)
                    .where(APP_USER.ID.eq(Long.valueOf(appUserIdString))).fetchOne();
                if (appUserRecord != null) {
                  JooqUserDetails userDetails = new JooqUserDetails(appUserRecord);
                  this.userDetailsChecker.check(userDetails);

                  SecurityContextHolder.getContext().setAuthentication(
                      new UsernamePasswordAuthenticationToken(userDetails, null,
                          userDetails.getAuthorities()));
                }
              }
              catch (UsernameNotFoundException | LockedException | DisabledException
                  | AccountExpiredException | CredentialsExpiredException e) {
                // ignore this
              }
            }
          }
        }
      }
    }

    filterChain.doFilter(servletRequest, servletResponse);
  }
}