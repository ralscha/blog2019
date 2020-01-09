package ch.rasc.stateless.config;

import java.time.Duration;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app")
@Component
@Validated
public class AppProperties {
  private boolean secureCookie;

  @NotEmpty
  @Length(min = 3, max = 3)
  private String instanceNo;

  @NotNull
  private Duration cookieMaxAge;

  public String getInstanceNo() {
    return this.instanceNo;
  }

  public void setInstanceNo(String instanceNo) {
    this.instanceNo = instanceNo;
  }

  public Duration getCookieMaxAge() {
    return this.cookieMaxAge;
  }

  public void setCookieMaxAge(Duration cookieMaxAge) {
    this.cookieMaxAge = cookieMaxAge;
  }

  public boolean isSecureCookie() {
    return this.secureCookie;
  }

  public void setSecureCookie(boolean secureCookie) {
    this.secureCookie = secureCookie;
  }

}
