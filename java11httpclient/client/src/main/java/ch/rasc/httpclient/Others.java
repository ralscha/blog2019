package ch.rasc.httpclient;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;

public class Others {

	public static void main(String[] args) {

		// var request = HttpRequest.newBuilder().GET()...
		// var request = HttpRequest.newBuilder().POST(BodyPublishers.ofString("the body")).....
	
		// var request = HttpRequest.newBuilder().DELETE().build();
		// var request = HttpRequest.newBuilder().PUT(BodyPublishers.ofString("the body")).....

	  var request = HttpRequest.newBuilder(URI.create("https://localhost:8443/headers"))
      			        .method("HEAD", BodyPublishers.noBody())
      			        .build();
	}

}
