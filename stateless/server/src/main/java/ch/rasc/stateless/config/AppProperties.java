package ch.rasc.stateless.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {
  private boolean secureCookie;

  private Duration cookieMaxAge;

  private String keyPath;

  public boolean isSecureCookie() {
    return this.secureCookie;
  }

  public void setSecureCookie(boolean secureCookie) {
    this.secureCookie = secureCookie;
  }

  public Duration getCookieMaxAge() {
    return this.cookieMaxAge;
  }

  public void setCookieMaxAge(Duration cookieMaxAge) {
    this.cookieMaxAge = cookieMaxAge;
  }

  public String getKeyPath() {
    return this.keyPath;
  }

  public void setKeyPath(String keyPath) {
    this.keyPath = keyPath;
  }

}
