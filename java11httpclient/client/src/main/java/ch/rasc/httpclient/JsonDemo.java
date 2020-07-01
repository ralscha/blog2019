package ch.rasc.httpclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

public class JsonDemo {

  public static void main(String[] args) throws IOException, InterruptedException {
    get();
    post();
  }

  private static void get() throws IOException, InterruptedException {
    Jsonb jsonb = JsonbBuilder.create();
    
    var client = HttpClient.newHttpClient();
    var request = HttpRequest.newBuilder(URI.create("https://localhost:8443/user"))
    				        .build();

    HttpResponse<User> response = client.send(request,
    			JsonBodyHandler.jsonBodyHandler(jsonb, User.class));
    System.out.println(response.body());
  }

  private static void post() throws IOException, InterruptedException {
    Jsonb jsonb = JsonbBuilder.create();
    var client = HttpClient.newHttpClient();

    User user = new User(2, "Mr. Client");
    var request = HttpRequest.newBuilder()
            				.POST(BodyPublishers.ofString(jsonb.toJson(user)))
            				.uri(URI.create("https://localhost:8443/saveUser"))
            				.header("Content-Type", "application/json")
            				.build();

    HttpResponse<Void> response = client.send(request, BodyHandlers.discarding());
    System.out.println(response.statusCode());
  }

}
