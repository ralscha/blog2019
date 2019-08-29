package ch.rasc.jsonp;

import java.util.Map;

import javax.json.Json;
import javax.json.stream.JsonGenerator;

public class StreamWrite {
  public static void main(String[] args) {

    var properties = Map.of(JsonGenerator.PRETTY_PRINTING, Boolean.FALSE);
    try (JsonGenerator jg = Json.createGeneratorFactory(properties)
        .createGenerator(System.out)) {

      jg.writeStartObject()
          .write("id", 1234)
          .write("active", true)
          .write("name", "Duke")
          .writeNull("password")
          .writeStartArray("roles")
             .write("admin")
             .write("user")
             .write("operator")
          .writeEnd()
          .writeStartArray("phoneNumbers")
             .writeStartObject()
               .write("type", "mobile")
               .write("number", "111-111-1111")
             .writeEnd()
             .writeStartObject()
               .write("type", "home")
               .write("number", "222-222-2222")
             .writeEnd()
          .writeEnd()
        .writeEnd();
    }
  }
}
