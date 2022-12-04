package ch.rasc.stateless.config.security;

import static ch.rasc.stateless.db.tables.AppSession.APP_SESSION;
import static ch.rasc.stateless.db.tables.AppUser.APP_USER;

import java.time.LocalDateTime;

import org.jooq.DSLContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.rasc.stateless.config.AppProperties;
import ch.rasc.stateless.db.tables.records.AppSessionRecord;
import ch.rasc.stateless.db.tables.records.AppUserRecord;

@RestController
public class AuthController {

  private final PasswordEncoder passwordEncoder;

  private final DSLContext dsl;

  private final TokenService tokenService;

  private final AppProperties appProperties;

  private final String userNotFoundEncodedPassword;

  public AuthController(PasswordEncoder passwordEncoder, DSLContext dsl,
      TokenService tokenService, AppProperties appProperties) {
    this.passwordEncoder = passwordEncoder;
    this.dsl = dsl;
    this.tokenService = tokenService;
    this.appProperties = appProperties;
    this.userNotFoundEncodedPassword = this.passwordEncoder
        .encode("userNotFoundPassword");
  }

  @GetMapping("/authenticate")
  @PreAuthorize("isFullyAuthenticated()")
  public String authenticate(@AuthenticationPrincipal AppUserDetail user) {
    return user.getAuthorities().iterator().next().getAuthority();
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(String username, String password) {

    AppUserRecord appUserRecord = this.dsl.selectFrom(APP_USER)
        .where(APP_USER.EMAIL.eq(username)).fetchOne();

    if (appUserRecord != null) {
      boolean pwMatches = this.passwordEncoder.matches(password,
          appUserRecord.getPasswordHash());
      if (pwMatches && appUserRecord.getEnabled().booleanValue()) {

        String sessionId = this.tokenService.createToken();

        AppSessionRecord record = this.dsl.newRecord(APP_SESSION);
        record.setId(sessionId);
        record.setAppUserId(appUserRecord.getId());
        record.setValidUntil(
            LocalDateTime.now().plus(this.appProperties.getCookieMaxAge()));
        record.store();

        ResponseCookie cookie = ResponseCookie
            .from(AuthCookieFilter.COOKIE_NAME, sessionId)
            .maxAge(this.appProperties.getCookieMaxAge()).sameSite("Strict").path("/")
            .httpOnly(true).secure(this.appProperties.isSecureCookie()).build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(appUserRecord.getAuthority());
      }
    }
    else {
      this.passwordEncoder.matches(password, this.userNotFoundEncodedPassword);
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

}
