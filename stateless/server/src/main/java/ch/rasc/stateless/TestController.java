package ch.rasc.stateless;

import static ch.rasc.stateless.db.tables.AppUser.APP_USER;

import org.jooq.DSLContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.rasc.stateless.config.security.AppUserDetail;

@RestController
public class TestController {

  private final DSLContext dsl;

  public TestController(DSLContext dsl) {
    this.dsl = dsl;
  }

  @GetMapping("/message")
  @PreAuthorize("isAuthenticated()")
  public String message(@AuthenticationPrincipal AppUserDetail user) {
    if ("admin@test.com".equals(user.getEmail())) {
      return "This is a message for the administrator";
    }
    return "This is a message for all users";
  }

  @GetMapping("/admin")
  @PreAuthorize("hasAuthority('ADMIN')")
  public String onlyForAdmin() {
    return "only for admin";
  }

  @GetMapping("/disable")
  @PreAuthorize("hasAuthority('ADMIN')")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void disableUser() {
    this.dsl.update(APP_USER).set(APP_USER.ENABLED, false)
        .where(APP_USER.EMAIL.eq("user@test.com")).execute();
  }

  @GetMapping("/enable")
  @PreAuthorize("hasAuthority('ADMIN')")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void enableUser() {
    this.dsl.update(APP_USER).set(APP_USER.ENABLED, true)
        .where(APP_USER.EMAIL.eq("user@test.com")).execute();
  }

  @GetMapping("/isEnabled")
  @PreAuthorize("hasAuthority('ADMIN')")
  public boolean isEnabled() {
    return this.dsl.select(APP_USER.ENABLED).from(APP_USER)
        .where(APP_USER.EMAIL.eq("user@test.com")).fetchOne().value1();
  }
}
