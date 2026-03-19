package ch.rasc.email;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.ServerSetup;

import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@SpringBootTest
class EmailApplicationTests {

  @Autowired
  private EmailService emailService;

  @Autowired
  private AppProperties appProperties;

  @RegisterExtension
  static GreenMailExtension greenMail = new GreenMailExtension(
      new ServerSetup(2525, "127.0.0.1", ServerSetup.PROTOCOL_SMTP))
      .withPerMethodLifecycle(false);

  @AfterEach
  public void cleanup() throws FolderException {
    greenMail.purgeEmailFromAllMailboxes();
  }

  @Test
  void testSendEmail() throws MessagingException, IOException {
    this.emailService.sendEmail();

    assertThat(greenMail.waitForIncomingEmail(1)).isTrue();

    MimeMessage testMessage = greenMail.getReceivedMessages()[0];
    assertThat(testMessage.getSubject()).isEqualTo("Test Email");
    assertThat(testMessage.getRecipients(RecipientType.TO)[0].toString())
      .isEqualTo("developer@test.com");
    assertThat(testMessage.getFrom()[0].toString())
      .isEqualTo(this.appProperties.getDefaultEmailSender());

    String emailContent = (String) testMessage.getContent();
    assertThat(emailContent.replaceAll("\\r\\n|\\r|\\n", ""))
      .isEqualTo("<h1>Hello World</h1>");
  }

}
