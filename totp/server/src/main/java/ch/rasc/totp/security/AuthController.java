package ch.rasc.totp.security;

import static ch.rasc.totp.db.tables.AppUser.APP_USER;

import javax.validation.constraints.NotEmpty;

import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Base32;
import org.jooq.DSLContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.passpol.PasswordPolicy;
import com.codahale.passpol.Status;

@RestController
@Validated
public class AuthController {

  private final DSLContext dsl;

  private final PasswordPolicy passwordPolicy;

  private final PasswordEncoder passwordEncoder;

  public AuthController(DSLContext dsl, PasswordPolicy passwordPolicy,
      PasswordEncoder passwordEncoder) {
    this.dsl = dsl;
    this.passwordPolicy = passwordPolicy;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping("/authenticate")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void authenticate() {
    // nothing here
  }

  @PostMapping("/signup")
  public SignupResponse signup(@RequestParam("username") @NotEmpty String username,
      @RequestParam("password") @NotEmpty String password,
      @RequestParam("totp") boolean totp) {

    // cancel if the user is already registered
    int count = this.dsl.selectCount().from(APP_USER)
        .where(APP_USER.USERNAME.equalIgnoreCase(username)).fetchOne(0, int.class);
    if (count > 0) {
      return new SignupResponse(
          ch.rasc.totp.security.SignupResponse.Status.USERNAME_TAKEN);
    }

    Status status = this.passwordPolicy.check(password);
    if (status != Status.OK) {
      return new SignupResponse(
          ch.rasc.totp.security.SignupResponse.Status.WEAK_PASSWORD);
    }

    if (totp) {
      String secret = Base32.random();

      this.dsl
          .insertInto(APP_USER, APP_USER.USERNAME, APP_USER.PASSWORD_HASH,
              APP_USER.ENABLED, APP_USER.SECRET)
          .values(username, this.passwordEncoder.encode(password), false, secret)
          .execute();
      return new SignupResponse(ch.rasc.totp.security.SignupResponse.Status.OK, username,
          secret);
    }

    this.dsl
        .insertInto(APP_USER, APP_USER.USERNAME, APP_USER.PASSWORD_HASH, APP_USER.ENABLED,
            APP_USER.SECRET)
        .values(username, this.passwordEncoder.encode(password), true, null).execute();
    return new SignupResponse(ch.rasc.totp.security.SignupResponse.Status.OK);
  }

  @PostMapping("/signup-confirm-secret")
  public boolean signupConfirmSecret(@RequestParam("username") String username,
      @RequestParam("code") @NotEmpty String code) {

    var record = this.dsl.select(APP_USER.ID, APP_USER.SECRET).from(APP_USER)
        .where(APP_USER.USERNAME.eq(username)).fetchOne();
    if (record != null) {
      String secret = record.get(APP_USER.SECRET);
      Totp totp = new Totp(secret);
      if (totp.verify(code)) {
        this.dsl.update(APP_USER).set(APP_USER.ENABLED, true)
            .where(APP_USER.ID.eq(record.get(APP_USER.ID))).execute();
        return true;
      }
    }

    return false;
  }

}
