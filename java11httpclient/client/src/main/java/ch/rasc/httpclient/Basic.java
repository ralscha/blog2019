package ch.rasc.httpclient;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class Basic {

  static class BasicAuthenticator extends Authenticator {
    private final String name;
    private final String password;

    private BasicAuthenticator(String name, String password) {
      this.name = name;
      this.password = password;
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(this.name, this.password.toCharArray());
    }
  }

  public static void main(String[] args) throws IOException, InterruptedException {

    var client = HttpClient.newBuilder()
    			        .authenticator(new BasicAuthenticator("user", "password"))
    			        .build();

    /* OR
    Authenticator.setDefault(new BasicAuthenticator("user", "password"));
    var client = HttpClient.newBuilder()
                    .authenticator(Authenticator.getDefault())
                    .build();
    */

    var request = HttpRequest.newBuilder()
      			    	  .GET()
      			        .uri(URI.create("https://localhost:8443/secret"))
      			        .build();

    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    System.out.println(response.statusCode());
    System.out.println(response.body());

    // second request
    request = HttpRequest.newBuilder()
          			.GET()
          			.uri(URI.create("https://localhost:8443/secret2"))
          			.build();

    response = client.send(request, BodyHandlers.ofString());
    System.out.println(response.statusCode());
    System.out.println(response.body());
  }

}
