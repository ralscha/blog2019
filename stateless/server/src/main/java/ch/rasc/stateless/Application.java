package ch.rasc.stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.context.request.WebRequest;

@SpringBootApplication
@EnableScheduling
public class Application {

  public static final Logger log = LoggerFactory.getLogger("app");

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
  
}
