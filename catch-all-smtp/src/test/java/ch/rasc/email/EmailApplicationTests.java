package ch.rasc.email;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

@SpringBootTest
class EmailApplicationTests {

  @Autowired
  private EmailService emailService;

  @Autowired
  private AppProperties appProperties;

  private static GreenMail greenMail;

  @BeforeAll
  public static void setupSMTP() {
    greenMail = new GreenMail(new ServerSetup(2525, "127.0.0.1", "smtp"));
    greenMail.start();
  }

  @AfterAll
  public static void tearDownSMTP() {
    greenMail.stop();
  }

  @AfterEach
  public void cleanup() throws FolderException {
    greenMail.purgeEmailFromAllMailboxes();
  }

  @Test
  void testSendEmail() throws MessagingException, IOException {
    this.emailService.sendEmail();

    boolean ok = greenMail.waitForIncomingEmail(1);
    // boolean ok = greenMail.waitForIncomingEmail(10_000, 1);

    if (ok) {
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
    else {
      Assertions.fail("email not sent");
    }
  }

}
