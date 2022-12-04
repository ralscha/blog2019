package ch.rasc.h2demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.PushBuilder;

@SpringBootApplication
@Controller
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @ResponseBody
  @GetMapping("/")
  public String helloWorld() {
    return "Hello World";
  }

  @GetMapping("/withoutPush")
  public String withoutPush() {
    return "index";
  }

  @GetMapping("/withPush")
  public String withPush(PushBuilder pushBuilder) {
    if (pushBuilder != null) {
      pushBuilder.path("cat.webp").push();
    }
    return "index";
  }

}
