package ch.rasc.jsonp;

import java.io.StringReader;

import jakarta.json.Json;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

public class Merge {

  public static void main(String[] args) {
    JsonObject source = Json.createObjectBuilder().add("id", 1234).add("active", true)
        .add("roles", Json.createArrayBuilder().add("admin")).build();
    System.out.println(source);
    // {"id":1234,"active":true,"roles":["admin"]}

    JsonObject target = Json.createObjectBuilder().add("id", 1234)
        .add("roles", Json.createArrayBuilder().add("admin").add("dev")).build();
    System.out.println(target);
    // {"id":1234,"roles":["admin","dev"]}

    JsonMergePatch mergePatch = Json.createMergeDiff(source, target);
    System.out.println(mergePatch.toJsonValue());
    // {"active":null,"roles":["admin","dev"]}

    try (JsonReader reader = Json.createReader(
        new StringReader("{\"active\":null,\"roles\":[\"admin\",\"dev\"]}"))) {
      JsonMergePatch createdMergePatch = Json.createMergePatch(reader.readValue());

      JsonValue modifiedSource = createdMergePatch.apply(source);
      System.out.println(modifiedSource);
      // {"id":1234,"roles":["admin","dev"]}
    }

  }

}
