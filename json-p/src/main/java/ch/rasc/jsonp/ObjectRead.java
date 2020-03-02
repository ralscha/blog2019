package ch.rasc.jsonp;

import java.io.StringReader;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;

public class ObjectRead {

  public static void main(String[] args) {

    String input = "{\"id\":1234,\"active\":true,\"name\":\"Duke\",\"password\":null,\"roles\":[\"admin\",\"user\",\"operator\"],\"phoneNumbers\":[{\"type\":\"mobile\",\"number\":\"111-111-1111\"},{\"type\":\"home\",\"number\":\"222-222-2222\"}]}";

    try (JsonReader reader = Json.createReader(new StringReader(input))) {
      JsonObject root = reader.readObject();
      int id = root.getInt("id");
      boolean active = root.getBoolean("active");
      String name = root.getString("name");

      System.out.println("id: " + id);
      System.out.println("active: " + active);
      System.out.println("name: " + name);

      JsonValue password = root.get("password");
      if (password == JsonValue.NULL) {
        System.out.println("password is null");
      }

      JsonArray roles = root.getJsonArray("roles");
      for (int i = 0; i < roles.size(); i++) {
        String role = roles.getString(i);
        System.out.println("  " + role);
      }

      if (!root.containsKey("firstName")) {
        System.out.println("does not contain firstName");
      }

      // String firstName = root.getString("firstName");
      // throws NullPointerException

      JsonArray phoneNumbers = root.getJsonArray("phoneNumbers");
      for (JsonValue phoneNumber : phoneNumbers) {
        if (phoneNumber.getValueType() == ValueType.OBJECT) {
          JsonObject obj = phoneNumber.asJsonObject();
          System.out.println(obj.getString("type"));
          System.out.println(obj.getString("number"));
        }
      }
    }

  }
}
