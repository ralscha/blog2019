package ch.rasc.jsonp;

import java.util.Map;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonWriter;
import jakarta.json.stream.JsonGenerator;

public class ObjectWrite {
  public static void main(String[] args) {
    JsonObject model = Json.createObjectBuilder().add("id", 1234).add("active", true)
        .add("name", "Duke").addNull("password")
        .add("roles", Json.createArrayBuilder().add("admin").add("user").add("operator"))
        .add("phoneNumbers",
            Json.createArrayBuilder()
                .add(Json.createObjectBuilder().add("type", "mobile").add("number",
                    "111-111-1111"))
                .add(Json.createObjectBuilder().add("type", "home").add("number",
                    "222-222-2222")))
        .build();

    try (JsonWriter defaultWriter = Json.createWriter(System.out)) {
      defaultWriter.write(model);
    }

    Map<String, Boolean> properties = Map.of(JsonGenerator.PRETTY_PRINTING, Boolean.TRUE);
    try (JsonWriter customWriter = Json.createWriterFactory(properties)
        .createWriter(System.out)) {
      customWriter.write(model);
    }
  }
}
