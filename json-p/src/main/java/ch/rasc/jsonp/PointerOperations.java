package ch.rasc.jsonp;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonPointer;
import javax.json.JsonValue;

public class PointerOperations {

  public static void main(String[] args) {
    JsonObject jsonObject = Json.createObjectBuilder().add("id", 1234).add("active", true)
        .addNull("password").add("roles", Json.createArrayBuilder().add("admin")).build();
    System.out.println(jsonObject);
    // {"id":1234,"active":true,"password":null,"roles":["admin"]}

    // add
    JsonPointer emailPointer = Json.createPointer("/email");
    JsonObject modifiedJsonObject = emailPointer.add(jsonObject,
        Json.createValue("test@test.com"));
    System.out.println(modifiedJsonObject);
    // {"id":1234,"active":true,"password":null,"roles":["admin"],"email":"test@test.com"}

    // remove
    JsonPointer passwordPointer = Json.createPointer("/password");
    modifiedJsonObject = passwordPointer.remove(jsonObject);
    System.out.println(modifiedJsonObject);
    // {"id":1234,"active":true,"roles":["admin"]}

    // replace
    JsonPointer activePointer = Json.createPointer("/active");
    modifiedJsonObject = activePointer.replace(jsonObject, JsonValue.FALSE);
    System.out.println(modifiedJsonObject);
    // {"id":1234,"active":false,"password":null,"roles":["admin"]}

    // containsValue
    System.out.println(activePointer.containsValue(jsonObject));

    JsonPointer lastNamePointer = Json.createPointer("/lastName");
    if (!lastNamePointer.containsValue(jsonObject)) {
      System.out.println("lastName does not exist");
    }

    // Arrays
    JsonPointer rolesPointer = Json.createPointer("/roles");
    modifiedJsonObject = rolesPointer.add(jsonObject,
        Json.createArrayBuilder().add("admin").add("dev").build());
    System.out.println(modifiedJsonObject);
    // {"id":1234,"active":true,"password":null,"roles":["admin","dev"]}

    JsonPointer addRolesPointer = Json.createPointer("/roles/-");
    modifiedJsonObject = addRolesPointer.add(jsonObject, Json.createValue("dev"));
    System.out.println(modifiedJsonObject);
    // {"id":1234,"active":true,"password":null,"roles":["admin","dev"]}
  }

}
