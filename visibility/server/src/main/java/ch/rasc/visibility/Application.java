package ch.rasc.visibility;

import java.util.Random;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@CrossOrigin
public class Application {

  private final Random random = new Random();

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @GetMapping("/hidden")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void hidden() {
    System.out.println("Page hidden");
  }

  @GetMapping("/visible")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void visible() {
    System.out.println("Page visible");
  }

  @GetMapping("/poll")
  public int poll() {
    System.out.println(System.currentTimeMillis() + ": poll called");
    return this.random.nextInt(100);
  }

}
