package ch.rasc.ratelimit;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class Client {

  private final static String SERVER_1 = "http://localhost:8080";
  private final static String SERVER_2 = "http://localhost:8081";

  private final static HttpClient httpClient = HttpClient.newHttpClient();

  public static void main(String[] args) {
    top1Client();
    // topNClient();
    // lastClient();
    // placeClient();
    // depthClient();
    // magClient();
    // bucketClient();
  }

  private static void top1Client() {
    System.out.println("/top1");
    try {
      for (int i = 0; i < 11; i++) {
        Builder builder = HttpRequest.newBuilder().uri(URI.create(SERVER_1 + "/top1"))
            .GET();
        HttpRequest request = builder.build();
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        System.out.println(response.statusCode());
      }
    }
    catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static void topNClient() {
    System.out.println("/top/3");
    for (int i = 0; i < 11; i++) {
      get(SERVER_1 + "/top/3");
    }
  }

  private static void lastClient() {
    System.out.println("/last");
    for (int i = 0; i < 11; i++) {
      get(SERVER_1 + "/last");
    }
  }

  private static void placeClient() {
    System.out.println("/place/Alaska");
    for (int i = 0; i < 4; i++) {
      get(SERVER_1 + "/place/Alaska");
    }
    for (int i = 0; i < 4; i++) {
      get(SERVER_2 + "/place/Alaska");
    }
  }

  private static void depthClient() {
    System.out.println("Free");
    for (int i = 0; i < 12; i++) {
      get(SERVER_1 + "/depth/0.0/1");
    }

    System.out.println("Client: 20128");
    for (int i = 0; i < 52; i++) {
      get(SERVER_1 + "/depth/0.0/1", "20128");
    }
    System.out.println("Client: 20343");
    for (int i = 0; i < 52; i++) {
      get(SERVER_1 + "/depth/0.0/1", "20343");
    }
    System.out.println("Client: 11223");
    for (int i = 0; i < 102; i++) {
      get(SERVER_1 + "/depth/0.0/1", "11223");
    }

    System.out.println("SERVER2");
    get(SERVER_2 + "/depth/0.0/1");
    get(SERVER_2 + "/depth/0.0/1", "20128");
    get(SERVER_2 + "/depth/0.0/1", "20343");
    get(SERVER_2 + "/depth/0.0/1", "11223");
  }

  private static void magClient() {
    System.out.println("Free");
    for (int i = 0; i < 12; i++) {
      get(SERVER_1 + "/mag/4/6");
    }

    System.out.println("Client: 20128");
    for (int i = 0; i < 52; i++) {
      get(SERVER_1 + "/mag/4/6", "20128");
    }
    System.out.println("Client: 20343");
    for (int i = 0; i < 52; i++) {
      get(SERVER_1 + "/mag/4/6", "20343");
    }
    System.out.println("Client: 11223");
    for (int i = 0; i < 102; i++) {
      get(SERVER_1 + "/mag/4/6", "11223");
    }

    System.out.println("SERVER2");
    get(SERVER_2 + "/mag/4/6");
    get(SERVER_2 + "/mag/4/6", "20128");
    get(SERVER_2 + "/mag/4/6", "20343");
    get(SERVER_2 + "/mag/4/6", "11223");
  }

  private static void get(String url) {
    get(url, null);
  }

  private static void get(String url, String apiKey) {
    try {
      Builder builder = HttpRequest.newBuilder().uri(URI.create(url)).GET();
      if (apiKey != null) {
        builder.header("X-api-key", apiKey);
      }
      HttpRequest request = builder.build();
      HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
      System.out.print(response.statusCode());
      if (response.statusCode() == 200) {
        String remaining = response.headers().firstValue("X-Rate-Limit-Remaining")
            .orElse(null);
        System.out.println(" Remaining: " + remaining);
      }
      else {
        String retry = response.headers()
            .firstValue("X-Rate-Limit-Retry-After-Milliseconds").orElse(null);
        System.out.println(" retry after milliseconds: " + retry);
      }
    }
    catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

}
