package ch.rasc.bigint;

import java.math.BigInteger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @GetMapping("/fetchData1")
  public Payload fetchData1() {
    long mersenne8 = (long) (Math.pow(2, 31) - 1);
    var mersenne9 = new BigInteger("2").pow(61).subtract(BigInteger.ONE);
    return new Payload(1, mersenne8, mersenne9);
  }

  @GetMapping("/fetchData2")
  public Payload fetchData2() {
    var mersenne15 = new BigInteger("2").pow(1279).subtract(BigInteger.ONE);
    return new Payload(2, 9007199254740991L, mersenne15);
  }

  @PostMapping("/storeData")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void postData(@RequestBody Payload payload) {
    System.out.println(payload);
  }

}
