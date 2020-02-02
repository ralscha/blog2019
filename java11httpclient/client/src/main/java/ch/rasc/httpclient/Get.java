package ch.rasc.httpclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import io.mikael.urlbuilder.UrlBuilder;

public class Get {

  public static void main(String[] args) throws IOException, InterruptedException {
    sync();
    async().join(); // join blocks the calling thread. only for demo purposes

    httpHeaders();
    queryParameters();
    copy();

    concurrentRequests(List.of("/one", "/two", "/three", "/four"));
    compress();
  }

  private static void concurrentRequests(List<String> paths) {
    var client = HttpClient.newHttpClient();

    List<HttpRequest> requests = paths.stream()
        .map(path -> "https://localhost:8443" + path)
        .map(URI::create)
        .map(uri -> HttpRequest.newBuilder(uri).build())
        .collect(Collectors.toList());
        
 
    
    CompletableFuture<?>[] responses = requests.stream()
        .map(request -> client.sendAsync(request, BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .exceptionally(e -> "Error: " + e.getMessage())
            .thenAccept(System.out::println))
        .toArray(CompletableFuture<?>[]::new);
        
    // for demo purposes, blocks calling thread
    CompletableFuture.allOf(responses).join();

  }

  public static void sync() {
    var client = HttpClient.newHttpClient();
    var request = HttpRequest.newBuilder()
        				  .GET()
    			        .uri(URI.create("https://localhost:8443/helloworld"))
    			        .timeout(Duration.ofSeconds(15))
    			        .build();

    try {
      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
      printResponse(response);
    }
    catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static CompletableFuture<Void> async() {
    var client = HttpClient.newHttpClient();
    var request = HttpRequest.newBuilder()
            				.GET()
            				.uri(URI.create("https://localhost:8443/helloworld"))
            				.build();

    CompletableFuture<HttpResponse<String>> future = client.sendAsync(request,
        BodyHandlers.ofString());

    return future.thenApply(response -> {
      printResponse(response);
      return response;
    }).thenApply(HttpResponse::body)
      .exceptionally(e -> "Error: " + e.getMessage())
      .thenAccept(System.out::println);
  }

  public static void httpHeaders() {
    var client = HttpClient.newHttpClient();
    var request = HttpRequest.newBuilder()
          				  .GET()
      			        .uri(URI.create("https://localhost:8443/headers"))
      			        .header("X-Auth", "authtoken")
      			        .headers("X-Custom1", "value1", "X-Custom2", "value2")
      			        .setHeader("X-Auth", "overwrite authtoken")
      			        .build();

    try {
      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

      for (String value : response.headers().allValues("X-Custom-Header")) {
        System.out.println(value);
      }

      String firstValue = response.headers().firstValue("X-Custom-Header").orElse("");
      long time = response.headers().firstValueAsLong("X-Time").orElse(-1L);

      System.out.println(firstValue);
      System.out.println(time);

      printResponse(response);
    }
    catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void copy() {
    var client = HttpClient.newHttpClient();

    var builder = HttpRequest.newBuilder()
                  .GET()
                  .uri(URI.create("https://localhost:8443/headers"));

    var request1 = builder.copy().setHeader("X-Counter", "1").build();
    var request2 = builder.copy().setHeader("X-Counter", "2").build();

    CompletableFuture<HttpResponse<String>> future1 = client.sendAsync(request1,
        BodyHandlers.ofString());
    CompletableFuture<HttpResponse<String>> future2 = client.sendAsync(request2,
        BodyHandlers.ofString());

    // only for demo purposes, blocks the calling thread
    CompletableFuture.allOf(future1, future2).join();
  }

  public static void queryParameters() {
    var client = HttpClient.newHttpClient();
    var request = HttpRequest.newBuilder()
            				.GET()
            				.uri(URI.create("https://localhost:8443/helloworld?query=value"))
            				.build();

    try {
      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
      printResponse(response);
    }
    catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }

    URI uri = UrlBuilder.empty()
                  			.withScheme("https")
                  			.withHost("localhost")
                  			.withPort(8443)
                  			.withPath("helloworld")
                  			.addParameter("query", "value")
                  			.toUri();
    request = HttpRequest.newBuilder(uri).build();
    try {
      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
      printResponse(response);
    }
    catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }

  }

  public static void compress() throws IOException, InterruptedException {
    var client = HttpClient.newHttpClient();
    var request = HttpRequest.newBuilder()
            				.GET()
            				.header("Accept-Encoding", "gzip")
            				.uri(URI.create("https://localhost:8443/indexWithoutPush"))
            				.build();

    HttpResponse<InputStream> response = client.send(request, BodyHandlers.ofInputStream());

    String encoding = response.headers().firstValue("Content-Encoding").orElse("");
    if (encoding.equals("gzip")) {
      System.out.println("gzip compressed");
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      try (InputStream is = new GZIPInputStream(response.body()); var autoCloseOs = os) {
        is.transferTo(autoCloseOs);
      }
      System.out.println(new String(os.toByteArray(), StandardCharsets.UTF_8));
    }
    else {
      System.out.println("not compressed");
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      try (var is = response.body(); var autoCloseOs = os) {
        is.transferTo(autoCloseOs);
      }
      System.out.println(new String(os.toByteArray(), StandardCharsets.UTF_8));
    }
  }

  private static void printResponse(HttpResponse<?> response) {
    System.out.println("Response:");
    System.out.println("URI     : " + response.uri());
    System.out.println("Version : " + response.version());
    System.out.println("Status  : " + response.statusCode());
    System.out.println("Headers : " + response.headers());
    System.out.println("Body    : " + response.body());
    System.out.println("=======================================");
  }
}
