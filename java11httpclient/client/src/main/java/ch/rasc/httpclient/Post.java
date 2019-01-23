package ch.rasc.httpclient;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Post {

  public static void main(String[] args) {
    postString().join();
    postFormData().join();
  }

  private static CompletableFuture<Void> postString() {
    var client = HttpClient.newHttpClient();
    var request = HttpRequest.newBuilder()
            				.POST(BodyPublishers.ofString("this is a text"))
            				.uri(URI.create("https://localhost:8443/uppercase"))
            				.header("Conent-Type", "text/plain")
            				.build();

    return client.sendAsync(request, BodyHandlers.ofString())
      			        .thenApply(HttpResponse::body)
      			        .exceptionally(e -> "Error: " + e.getMessage())
      			        .thenAccept(System.out::println);
  }

  private static CompletableFuture<Void> postFormData() {
    var client = HttpClient.newHttpClient();

    Map<Object, Object> data = new HashMap<>();
    data.put("id", 1);
    data.put("name", "a name");
    data.put("ts", System.currentTimeMillis());

    var request = HttpRequest.newBuilder()
      			        .POST(ofFormData(data))
      			        .uri(URI.create("https://localhost:8443/formdata"))
      			        .header("Content-Type", "application/x-www-form-urlencoded")
      			        .build();

    return client.sendAsync(request, BodyHandlers.ofString())
      			        .thenApply(HttpResponse::body)
      			        .exceptionally(e -> "Error: " + e.getMessage())
      			        .thenAccept(System.out::println);
  }

  public static BodyPublisher ofFormData(Map<Object, Object> data) {
    var builder = new StringBuilder();
    for (Map.Entry<Object, Object> entry : data.entrySet()) {
      if (builder.length() > 0) {
        builder.append("&");
      }
      builder
          .append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
      builder.append("=");
      builder
          .append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
    }
    return BodyPublishers.ofString(builder.toString());
  }

}
