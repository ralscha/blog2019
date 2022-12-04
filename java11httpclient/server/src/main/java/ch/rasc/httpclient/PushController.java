package ch.rasc.httpclient;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.PushBuilder;

@Controller
public class PushController {

  @GetMapping("/indexWithoutPush")
  public String withoutPush() {
    return "index";
  }

  @GetMapping("/indexWithPush")
  public String withPush(PushBuilder pushBuilder) {
    if (pushBuilder != null) {
      pushBuilder.path("cat.webp").push();
    }
    return "index";
  }

}