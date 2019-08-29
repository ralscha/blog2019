package ch.rasc.jsonp;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonPatch;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class Patch {

  public static void main(String[] args) {

    JsonObject source = Json.createObjectBuilder().add("id", 1234).add("active", true)
        .add("roles", Json.createArrayBuilder().add("admin")).build();
    System.out.println(source);
    // {"id":1234,"active":true,"roles":["admin"]}

    JsonObject target = Json.createObjectBuilder().add("id", 4321)
        .add("roles", Json.createArrayBuilder().add("admin").add("dev")).build();
    System.out.println(target);
    // {"id":4321,"roles":["admin","dev"]}

    JsonPatch patch = Json.createDiff(source, target);
    System.out.println(patch.toString());
    // [{"op":"replace","path":"/id","value":4321},{"op":"remove","path":"/active"},{"op":"add","path":"/roles/1","value":"dev"}]

    try (JsonReader reader = Json.createReader(new StringReader(
        "[{\"op\":\"replace\",\"path\":\"/id\",\"value\":4321},{\"op\":\"remove\",\"path\":\"/active\"},{\"op\":\"add\",\"path\":\"/roles/1\",\"value\":\"dev\"}]"))) {
      JsonPatch createdPatch = Json.createPatch(reader.readArray());
      JsonValue modifiedSource = createdPatch.apply(source);
      System.out.println(modifiedSource);
      // {"id":4321,"roles":["admin","dev"]}
    }
    
    JsonPatch builtPatch = Json.createPatchBuilder()
                             .replace("/id", 4321)
                             .add("/roles/1", "dev")
                             .remove("/active")
                             .build();    
    JsonValue modifiedSource = builtPatch.apply(source);
    System.out.println(modifiedSource);
    // {"id":4321,"roles":["admin","dev"]}
    
    builtPatch = Json.createPatchBuilder()
      .add("/roles/-", "dev")
      .copy("/uuid", "/id")
      .move("/enabled", "/active")
      .build();
    modifiedSource = builtPatch.apply(source);
    System.out.println(modifiedSource);
    // {"id":1234,"roles":["admin","dev"],"uuid":1234,"enabled":true}
    
    try {
      builtPatch = Json.createPatchBuilder()
          .test("/id", 4321)
          .add("/roles/-", "dev")
          .copy("/uuid", "/id")
          .move("/enabled", "/active")
          .build();
      modifiedSource = builtPatch.apply(source);
      System.out.println(modifiedSource);
    }
    catch (JsonException e) {
      System.out.println(e.getMessage());
      // The JSON Patch operation 'test' failed for path '/id' and value '4321'
    }
  }

}
