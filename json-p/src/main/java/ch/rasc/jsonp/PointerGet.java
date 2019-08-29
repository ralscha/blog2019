package ch.rasc.jsonp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonPointer;
import javax.json.JsonReader;

public class PointerGet {
  public static void main(String[] args) throws IOException, InterruptedException {
    String url = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/1.0_month.geojson";

    var client = HttpClient.newHttpClient();
    var request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();

    HttpResponse<InputStream> response = client.send(request,
        BodyHandlers.ofInputStream());

    JsonPointer allPointer = Json.createPointer("/features");
    JsonPointer magPointer = Json.createPointer("/features/0/properties/mag");
    JsonPointer placePointer = Json.createPointer("/features/0/properties/place");

    try (JsonReader reader = Json.createReader(response.body())) {
      JsonObject root = reader.readObject();

      JsonArray all = (JsonArray) allPointer.getValue(root);
      System.out.println("number of earthquakes: " + all.size());

      System.out.println(magPointer.getValue(root));
      System.out.println(placePointer.getValue(root));
    }

  }
}
