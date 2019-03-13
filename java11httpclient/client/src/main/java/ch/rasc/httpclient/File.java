package ch.rasc.httpclient;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import io.mikael.urlbuilder.UrlBuilder;

public class File {

  public static void main(String[] args) throws IOException, InterruptedException {
    var url = "https://www.7-zip.org/a/7z1806-x64.exe";

    var client = HttpClient.newBuilder().build();
    var request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();

    Path localFile = Paths.get("7z.exe");
    HttpResponse<Path> response = client.send(request, BodyHandlers.ofFile(localFile));
    System.out.println(response);

    String virusTotalApiKey = "b3a4addb86c82979c20c3a469576e6ae81ba4d4bf3ab36d107a4f9cea5bf70bf";

    Map<Object, Object> data = new LinkedHashMap<>();
    data.put("apikey", virusTotalApiKey);
    data.put("file", localFile);
    String boundary = new BigInteger(256, new Random()).toString();

    request = HttpRequest.newBuilder()
              .header("Content-Type", "multipart/form-data;boundary=" + boundary)
              .POST(ofMimeMultipartData(data, boundary))
              .uri(URI.create("https://www.virustotal.com/vtapi/v2/file/scan"))
              .build();

    HttpResponse<String> vtResponse = client.send(request, BodyHandlers.ofString());

    try (JsonReader jsonReader = Json.createReader(new StringReader(vtResponse.body()))) {
      JsonObject jobj = jsonReader.readObject();
      String resource = jobj.getString("resource");
      URI uri = UrlBuilder.fromString("https://www.virustotal.com/vtapi/v2/file/report")
          .addParameter("apikey", virusTotalApiKey).addParameter("resource", resource)
          .toUri();

      HttpResponse<String> status = client.send(HttpRequest.newBuilder(uri).build(),
          BodyHandlers.ofString());
      
      System.out.println(status.body());
    }
  }

  public static BodyPublisher ofMimeMultipartData(Map<Object, Object> data,
      String boundary) throws IOException {
    var byteArrays = new ArrayList<byte[]>();
    byte[] separator = ("--" + boundary + "\r\nContent-Disposition: form-data; name=")
        .getBytes(StandardCharsets.UTF_8);
    for (Map.Entry<Object, Object> entry : data.entrySet()) {
      byteArrays.add(separator);

      if (entry.getValue() instanceof Path) {
        var path = (Path) entry.getValue();
        String mimeType = Files.probeContentType(path);
        byteArrays.add(("\"" + entry.getKey() + "\"; filename=\"" + path.getFileName()
            + "\"\r\nContent-Type: " + mimeType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        byteArrays.add(Files.readAllBytes(path));
        byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
      }
      else {
        byteArrays.add(("\"" + entry.getKey() + "\"\r\n\r\n" + entry.getValue() + "\r\n")
            .getBytes(StandardCharsets.UTF_8));
      }
    }
    byteArrays.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));
    return BodyPublishers.ofByteArrays(byteArrays);
  }

}
