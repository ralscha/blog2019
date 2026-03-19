package ch.rasc.stateless.config.security;

import static ch.rasc.stateless.db.tables.AppSession.APP_SESSION;
import static ch.rasc.stateless.db.tables.AppUser.APP_USER;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.jooq.DSLContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import ch.rasc.stateless.db.tables.records.AppUserRecord;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthCookieFilter extends OncePerRequestFilter {

  public final static String COOKIE_NAME = "authentication";

  private final DSLContext dsl;

  private final Cache<String, CachedAuthentication> userDetailsCache;

  public AuthCookieFilter(DSLContext dsl) {
    this.dsl = dsl;

    this.userDetailsCache = Caffeine.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES)
        .maximumSize(1_000).build();
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String sessionId = extractAuthenticationCookie(request);

    if (sessionId != null
        && SecurityContextHolder.getContext().getAuthentication() == null) {
      CachedAuthentication cachedAuthentication = this.userDetailsCache.get(sessionId,
          key -> loadAuthentication(sessionId));

      if (cachedAuthentication != null
          && cachedAuthentication.validUntil().isAfter(LocalDateTime.now())
          && cachedAuthentication.userDetails().isEnabled()) {
        SecurityContextHolder.getContext().setAuthentication(
            new UserAuthentication(cachedAuthentication.userDetails()));
      }
      else {
        this.userDetailsCache.invalidate(sessionId);
      }
    }

    filterChain.doFilter(request, response);
  }

  private CachedAuthentication loadAuthentication(String sessionId) {
    var record = this.dsl.select(APP_USER.asterisk(), APP_SESSION.VALID_UNTIL)
        .from(APP_USER).innerJoin(APP_SESSION).onKey()
        .where(APP_SESSION.ID.eq(sessionId))
        .and(APP_SESSION.VALID_UNTIL.gt(LocalDateTime.now())).fetchOne();
    if (record == null) {
      return null;
    }

    return new CachedAuthentication(record.into(AppUserRecord.class),
        record.get(APP_SESSION.VALID_UNTIL));
  }

  public static String extractAuthenticationCookie(
      HttpServletRequest httpServletRequest) {
    String sessionId = null;
    Cookie[] cookies = httpServletRequest.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (AuthCookieFilter.COOKIE_NAME.equals(cookie.getName())) {
          sessionId = cookie.getValue();
          break;
        }
      }
    }
    return sessionId;
  }

  private record CachedAuthentication(AppUserDetail userDetails,
      LocalDateTime validUntil) {

    private CachedAuthentication(AppUserRecord appUserRecord,
        LocalDateTime validUntil) {
      this(new AppUserDetail(appUserRecord), validUntil);
    }
  }
}