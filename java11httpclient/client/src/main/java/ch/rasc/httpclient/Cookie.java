package ch.rasc.httpclient;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class Cookie {
  public static void main(String[] args) throws IOException, InterruptedException {

    CookieHandler.setDefault(new CookieManager());

    var client = HttpClient.newBuilder()
    			        .cookieHandler(CookieHandler.getDefault())
    			        .build();

    //OR
    /*
    var client = HttpClient.newBuilder()
    			        .cookieHandler(new CookieManager())
    			        .build();
    */

    var request = HttpRequest.newBuilder()
      			        .GET()
      			        .uri(URI.create("https://localhost:8443/setCookie"))
      			        .build();

    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    System.out.println(response.statusCode());
    System.out.println(response.headers().firstValue("set-cookie"));

    request = HttpRequest.newBuilder()
    		        .GET()
    		        .uri(URI.create("https://localhost:8443/secondCookieRequest"))
    		        .build();

    response = client.send(request, BodyHandlers.ofString());
    System.out.println(response.statusCode());
    System.out.println(response.headers());
    System.out.println(response.body());
  }
}
