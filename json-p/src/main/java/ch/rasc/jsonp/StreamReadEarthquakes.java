package ch.rasc.jsonp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import jakarta.json.Json;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;

public class StreamReadEarthquakes {
  public static void main(String[] args) throws IOException, InterruptedException {

    String url = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/1.0_month.geojson";

    var client = HttpClient.newHttpClient();
    var request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();

    HttpResponse<InputStream> response = client.send(request,
        BodyHandlers.ofInputStream());

    try (JsonParser parser = Json.createParser(response.body())) {
      while (parser.hasNext()) {
        Event event = parser.next();
        if (event == Event.KEY_NAME) {
          String key = parser.getString();
          if (key.equals("mag")) {
            parser.next();
            System.out.println(parser.getBigDecimal());
          }
          else if (key.equals("place")) {
            parser.next();
            System.out.println(parser.getString());
            System.out.println();
          }
        }
      }
    }

  }
}
