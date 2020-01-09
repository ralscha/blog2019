package ch.rasc.stateless.config.security;

import static ch.rasc.stateless.db.tables.AppSession.APP_SESSION;
import static ch.rasc.stateless.db.tables.AppUser.APP_USER;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.jooq.DSLContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import ch.rasc.stateless.db.tables.records.AppUserRecord;

public class AuthCookieFilter extends GenericFilterBean {

  public final static String COOKIE_NAME = "authentication";

  private final DSLContext dsl;

  private final Cache<String, AppUserDetail> userDetailsCache;

  public AuthCookieFilter(DSLContext dsl) {
    this.dsl = dsl;

    this.userDetailsCache = Caffeine.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES)
        .maximumSize(1_000).build();
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {

    HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

    String sessionId = extractAuthenticationCookie(httpServletRequest);

    if (sessionId != null) {
      final String sId = sessionId;
      AppUserDetail userDetails = this.userDetailsCache.get(sessionId, key -> {
        var record = this.dsl.select(APP_USER.asterisk()).from(APP_USER)
            .innerJoin(APP_SESSION).onKey().where(APP_SESSION.ID.eq(sId)).fetchOne()
            .into(AppUserRecord.class);
        if (record != null) {
          return new AppUserDetail(record);
        }
        return null;
      });

      if (userDetails != null && userDetails.isEnabled()) {
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(userDetails));
      }
    }

    filterChain.doFilter(servletRequest, servletResponse);
  }

  public static String extractAuthenticationCookie(HttpServletRequest httpServletRequest) {
    String sessionId = null;
    Cookie[] cookies = httpServletRequest.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(AuthCookieFilter.COOKIE_NAME)) {
          sessionId = cookie.getValue();
          break;
        }
      }
    }
    return sessionId;
  }
}