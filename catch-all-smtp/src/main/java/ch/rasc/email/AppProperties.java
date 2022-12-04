package ch.rasc.email;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

@ConfigurationProperties(prefix = "app")
@Component
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
