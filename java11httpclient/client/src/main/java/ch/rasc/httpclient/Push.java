package ch.rasc.httpclient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpResponse.PushPromiseHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public class Push {

  public static void main(String[] args) {

    var client = HttpClient.newHttpClient();
    var request = HttpRequest.newBuilder()
        		    		.GET()
        		    		.uri(URI.create("https://localhost:8443/indexWithoutPush"))
        		    		.build();

    client.sendAsync(request, BodyHandlers.ofString())
        		.thenApply(HttpResponse::body)
        		.thenAccept(System.out::println)
        		.join(); // only for demo purposes, blocks calling thread

    request = HttpRequest.newBuilder()
      	    		.GET()
      	    		.uri(URI.create("https://localhost:8443/indexWithPush"))
      	    		.build();

    var asyncRequests = new CopyOnWriteArrayList<CompletableFuture<Void>>();

    PushPromiseHandler<byte[]> pph = (initial, pushRequest, acceptor) -> {
      CompletableFuture<Void> cf = acceptor.apply(BodyHandlers.ofByteArray())
          .thenAccept(response -> {
            System.out.println("Got pushed resource: " + response.uri());
            System.out.println("Body: " + response.body());
          });
      asyncRequests.add(cf);
    };

    client.sendAsync(request, BodyHandlers.ofByteArray(), pph)
	        .thenApply(HttpResponse::body)
	        .thenAccept(System.out::println)
	        .join();

    // block calling thread for demo purposes
    asyncRequests.forEach(CompletableFuture::join);
  }

}
