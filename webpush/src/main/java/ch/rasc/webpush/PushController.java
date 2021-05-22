package ch.rasc.webpush;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.rasc.webpush.dto.Notification;
import ch.rasc.webpush.dto.PushMessage;
import ch.rasc.webpush.dto.Subscription;
import ch.rasc.webpush.dto.SubscriptionEndpoint;

@RestController
public class PushController {

  private final ServerKeys serverKeys;

  private final CryptoService cryptoService;

  private final Map<String, Subscription> subscriptions = new ConcurrentHashMap<>();

  private final Map<String, Subscription> subscriptionsAngular = new ConcurrentHashMap<>();

  private String lastNumbersAPIFact = "";

  private final HttpClient httpClient;

  private final Algorithm jwtAlgorithm;

  private final ObjectMapper objectMapper;

  public PushController(ServerKeys serverKeys, CryptoService cryptoService,
      ObjectMapper objectMapper) {
    this.serverKeys = serverKeys;
    this.cryptoService = cryptoService;
    this.httpClient = HttpClient.newHttpClient();
    this.objectMapper = objectMapper;

    this.jwtAlgorithm = Algorithm.ECDSA256(this.serverKeys.getPublicKey(),
        this.serverKeys.getPrivateKey());
  }

  @GetMapping(path = "/publicSigningKey", produces = "application/octet-stream")
  public byte[] publicSigningKey() {
    return this.serverKeys.getPublicKeyUncompressed();
  }

  @GetMapping(path = "/publicSigningKeyBase64")
  public String publicSigningKeyBase64() {
    return this.serverKeys.getPublicKeyBase64();
  }

  @PostMapping("/subscribe")
  @ResponseStatus(HttpStatus.CREATED)
  public void subscribe(@RequestBody Subscription subscription) {
    this.subscriptions.put(subscription.getEndpoint(), subscription);
  }

  @PostMapping("/subscribeAngular")
  @ResponseStatus(HttpStatus.CREATED)
  public void subscribeAngular(@RequestBody Subscription subscription) {
    System.out.println("subscribe: " + subscription);
    this.subscriptionsAngular.put(subscription.getEndpoint(), subscription);
  }

  @PostMapping("/unsubscribe")
  public void unsubscribe(@RequestBody SubscriptionEndpoint subscription) {
    this.subscriptions.remove(subscription.getEndpoint());
  }

  @PostMapping("/unsubscribeAngular")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void unsubscribeAngular(@RequestBody SubscriptionEndpoint subscription) {
    System.out.println("unsubscribe: " + subscription);
    this.subscriptionsAngular.remove(subscription.getEndpoint());
  }

  @PostMapping("/isSubscribed")
  public boolean isSubscribed(@RequestBody SubscriptionEndpoint subscription) {
    return this.subscriptions.containsKey(subscription.getEndpoint());
  }

  @PostMapping("/isSubscribedAngular")
  public boolean isSubscribedAngular(@RequestBody SubscriptionEndpoint subscription) {
    return this.subscriptionsAngular.containsKey(subscription.getEndpoint());
  }

  @GetMapping(path = "/lastNumbersAPIFact")
  public String lastNumbersAPIFact() {
    return this.lastNumbersAPIFact;
  }

  @Scheduled(fixedDelay = 20_000)
  public void numberFact() {
    if (this.subscriptions.isEmpty()) {
      return;
    }

    try {
      HttpResponse<String> response = this.httpClient.send(
          HttpRequest.newBuilder(URI.create("http://numbersapi.com/random/date")).build(),
          BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        this.lastNumbersAPIFact = response.body();
        sendPushMessageToAllSubscribersWithoutPayload();
      }
    }
    catch (IOException | InterruptedException e) {
      Application.logger.error("fetch number fact", e);
    }
  }

  @Scheduled(fixedDelay = 30_000)
  public void chuckNorrisJoke() {
    if (this.subscriptions.isEmpty() && this.subscriptionsAngular.isEmpty()) {
      return;
    }

    try {
      HttpResponse<String> response = this.httpClient.send(HttpRequest
          .newBuilder(URI.create("https://api.icndb.com/jokes/random")).build(),
          BodyHandlers.ofString());
      if (response.statusCode() == 200) {
        Map<String, Object> jokeJson = this.objectMapper.readValue(response.body(),
            Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> value = (Map<String, Object>) jokeJson.get("value");
        int id = (int) value.get("id");
        String joke = (String) value.get("joke");

        sendPushMessageToAllSubscribers(this.subscriptions,
            new PushMessage("Chuck Norris Joke: " + id, joke));

        Notification notification = new Notification("Chuck Norris Joke: " + id);
        notification.setBody(joke);
        notification.setIcon("assets/chuck.png");

        sendPushMessageToAllSubscribers(this.subscriptionsAngular,
            Map.of("notification", notification));
      }
    }
    catch (IOException | InterruptedException e) {
      Application.logger.error("fetch chuck norris", e);
    }
  }

  private void sendPushMessageToAllSubscribersWithoutPayload() {
    Set<String> failedSubscriptions = new HashSet<>();
    for (Subscription subscription : this.subscriptions.values()) {
      boolean remove = sendPushMessage(subscription, null);
      if (remove) {
        failedSubscriptions.add(subscription.getEndpoint());
      }
    }
    failedSubscriptions.forEach(this.subscriptions::remove);
  }

  private void sendPushMessageToAllSubscribers(Map<String, Subscription> subs,
      Object message) throws JsonProcessingException {

    Set<String> failedSubscriptions = new HashSet<>();

    for (Subscription subscription : subs.values()) {
      try {
        byte[] result = this.cryptoService.encrypt(
            this.objectMapper.writeValueAsString(message),
            subscription.getKeys().getP256dh(), subscription.getKeys().getAuth(), 0);
        boolean remove = sendPushMessage(subscription, result);
        if (remove) {
          failedSubscriptions.add(subscription.getEndpoint());
        }
      }
      catch (InvalidKeyException | NoSuchAlgorithmException
          | InvalidAlgorithmParameterException | IllegalStateException
          | InvalidKeySpecException | NoSuchPaddingException | IllegalBlockSizeException
          | BadPaddingException e) {
        Application.logger.error("send encrypted message", e);
      }
    }

    failedSubscriptions.forEach(subs::remove);
  }

  /**
   * @return true if the subscription is no longer valid and can be removed, false if
   * everything is okay
   */
  private boolean sendPushMessage(Subscription subscription, byte[] body) {
    String origin = null;
    try {
      URL url = new URL(subscription.getEndpoint());
      origin = url.getProtocol() + "://" + url.getHost();
    }
    catch (MalformedURLException e) {
      Application.logger.error("create origin", e);
      return true;
    }

    Date today = new Date();
    Date expires = new Date(today.getTime() + 12 * 60 * 60 * 1000);

    String token = JWT.create().withAudience(origin).withExpiresAt(expires)
        .withSubject("mailto:example@example.com").sign(this.jwtAlgorithm);

    URI endpointURI = URI.create(subscription.getEndpoint());

    Builder httpRequestBuilder = HttpRequest.newBuilder();
    if (body != null) {
      httpRequestBuilder.POST(BodyPublishers.ofByteArray(body))
          .header("Content-Type", "application/octet-stream")
          .header("Content-Encoding", "aes128gcm");
    }
    else {
      httpRequestBuilder.POST(BodyPublishers.ofString(""));
      // httpRequestBuilder.header("Content-Length", "0");
    }

    HttpRequest request = httpRequestBuilder.uri(endpointURI).header("TTL", "180")
        .header("Authorization",
            "vapid t=" + token + ", k=" + this.serverKeys.getPublicKeyBase64())
        .build();
    try {
      HttpResponse<Void> response = this.httpClient.send(request,
          BodyHandlers.discarding());

      switch (response.statusCode()) {
      case 201:
        Application.logger.info("Push message successfully sent: {}",
            subscription.getEndpoint());
        break;
      case 404:
      case 410:
        Application.logger.warn("Subscription not found or gone: {}",
            subscription.getEndpoint());
        // remove subscription from our collection of subscriptions
        return true;
      case 429:
        Application.logger.error("Too many requests: {}", request);
        break;
      case 400:
        Application.logger.error("Invalid request: {}", request);
        break;
      case 413:
        Application.logger.error("Payload size too large: {}", request);
        break;
      default:
        Application.logger.error("Unhandled status code: {} / {}", response.statusCode(),
            request);
      }
    }
    catch (IOException | InterruptedException e) {
      Application.logger.error("send push message", e);
    }

    return false;
  }

}