package ch.rasc.httpclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class RedirectDemo {

  public static void main(String[] args) throws IOException, InterruptedException {
    never();
    normal();
  }

  private static void never() throws IOException, InterruptedException {
    var client = HttpClient.newHttpClient();
    var request = HttpRequest.newBuilder()
        		    		.GET()
        		    		.uri(URI.create("https://localhost:8443/redirect"))
        		    		.build();

    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    int sc = response.statusCode();
    String newLocation = response.headers().firstValue("Location").orElse(null);

    System.out.println(sc);
    System.out.println(newLocation);
  }

  private static void normal() throws IOException, InterruptedException {
    // redirect automatially
    var client = HttpClient.newBuilder().followRedirects(Redirect.NORMAL).build();
    var request = HttpRequest.newBuilder()
        		    		.GET()
        		    		.uri(URI.create("https://localhost:8443/redirect"))
        		    		.build();
    
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

    int sc = response.statusCode();
    String body = response.body();
    URI uri = response.uri();

    System.out.println(sc);
    System.out.println(body);
    System.out.println(uri);

  }

}
