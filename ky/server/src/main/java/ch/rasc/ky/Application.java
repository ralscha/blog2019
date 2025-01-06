package ch.rasc.ky;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@SpringBootApplication
@RestController
public class Application {

  private final Bucket bucket;

  Application() {
    this.bucket = Bucket.builder()
        .addLimit(limit -> limit.capacity(2).refillGreedy(2, Duration.ofSeconds(5)))
        .build();
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
      }
    };
  }

  @GetMapping(path = "/simple-get", produces = MediaType.TEXT_PLAIN_VALUE)
  public String simpleGetText() {
    return "hello world from text";
  }

  @GetMapping(path = "/simple-get", produces = MediaType.APPLICATION_JSON_VALUE)
  public String simpleGetJson() {
    return "{\"text\": \"hello world from json\"}";
  }

  @GetMapping(path = "/simple-get", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MultiValueMap<String, Object>> simpleGetFormData() {
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    HttpHeaders header = new HttpHeaders();
    header.setContentType(MediaType.TEXT_PLAIN);
    HttpEntity<String> part = new HttpEntity<>("hello world from form data", header);
    body.add("response", part);
    return new ResponseEntity<>(body, HttpStatus.OK);
  }

  @GetMapping(path = "/simple-get", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public byte[] simpleGetBinary() {
    return "hello world from binary".getBytes(StandardCharsets.UTF_8);
  }

  @PostMapping(path = "/simple-post")
  public Value simplePost(@RequestBody Value value) {
    return new Value(value.getValue().toUpperCase());
  }

  @PostMapping(path = "/multipart-post")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void multipartPost(@RequestParam("value1") int value1,
      @RequestParam("value2") String value2) {
    System.out.println("multipartPost");
    System.out.println("value1=" + value1);
    System.out.println("value2=" + value2);
  }

  @PostMapping(path = "/formurlencoded-post")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void urlencodedPost(@RequestParam("value1") int value1,
      @RequestParam("value2") String value2) {
    System.out.println("urlencodedPost");
    System.out.println("value1=" + value1);
    System.out.println("value2=" + value2);
  }

  @GetMapping("/retry-test")
  public ResponseEntity<String> retryTest() {
    return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
  }

  @GetMapping("/retry")
  public ResponseEntity<String> retry() {
    ConsumptionProbe probe = this.bucket.tryConsumeAndReturnRemaining(1);
    if (probe.isConsumed()) {
      return ResponseEntity.ok("OK");
    }

    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
        .header(HttpHeaders.RETRY_AFTER,
            Long.toString(
                TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()) + 1))
        .build();
  }

  @GetMapping("/timeout")
  public void timeout() throws InterruptedException {
    TimeUnit.SECONDS.sleep(20);
  }

  @GetMapping("/download")
  public void download(HttpServletResponse response)
      throws IOException, InterruptedException {
    response.addHeader(HttpHeaders.CONTENT_LENGTH, "1000000");
    response.addHeader(HttpHeaders.CONTENT_TYPE,
        MediaType.APPLICATION_OCTET_STREAM_VALUE);
    Random rand = new Random();
    @SuppressWarnings("resource")
    ServletOutputStream outputStream = response.getOutputStream();

    for (int i = 0; i < 100; i++) {
      byte[] bytes = new byte[10_000];
      rand.nextBytes(bytes);

      outputStream.write(bytes);
      TimeUnit.MILLISECONDS.sleep(250);
    }
  }

  @GetMapping("/always-fail")
  public void alwaysFail() {
    throw new RuntimeException("always fail");
  }

}
