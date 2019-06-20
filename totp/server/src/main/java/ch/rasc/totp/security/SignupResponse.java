package ch.rasc.totp.security;

public class SignupResponse {

  enum Status {
    OK, USERNAME_TAKEN, WEAK_PASSWORD
  }

  private final Status status;

  private final String username;

  private final String secret;

  public SignupResponse(Status status) {
    this(status, null, null);
  }

  public SignupResponse(Status status, String username, String secret) {
    this.status = status;
    this.username = username;
    this.secret = secret;
  }

  public Status getStatus() {
    return this.status;
  }

  public String getSecret() {
    return this.secret;
  }

  public String getUsername() {
    return this.username;
  }

}
