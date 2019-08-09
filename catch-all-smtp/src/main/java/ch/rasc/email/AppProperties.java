package ch.rasc.email;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app")
@Validated
public class AppProperties {

  @NotEmpty
  @Email
  private String defaultEmailSender;

  public String getDefaultEmailSender() {
    return this.defaultEmailSender;
  }

  public void setDefaultEmailSender(String defaultEmailSender) {
    this.defaultEmailSender = defaultEmailSender;
  }

}
