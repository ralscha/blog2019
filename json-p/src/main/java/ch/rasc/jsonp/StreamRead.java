package ch.rasc.jsonp;

import java.io.StringReader;

import jakarta.json.Json;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;

public class StreamRead {
  public static void main(String[] args) {

    String input = "{\"id\":1234,\"active\":true,\"name\":\"Duke\",\"password\":null,\"roles\":[\"admin\",\"user\",\"operator\"],\"phoneNumbers\":[{\"type\":\"mobile\",\"number\":\"111-111-1111\"},{\"type\":\"home\",\"number\":\"222-222-2222\"}]}";

    try (JsonParser parser = Json.createParser(new StringReader(input))) {
      while (parser.hasNext()) {
        Event event = parser.next();

        switch (event) {
        case START_OBJECT:
          System.out.println("START_OBJECT");
          break;
        case END_OBJECT:
          System.out.println("END_OBJECT");
          break;
        case START_ARRAY:
          System.out.println("START_ARRAY");
          break;
        case END_ARRAY:
          System.out.println("END_ARRAY");
          break;
        case KEY_NAME:
          System.out.print("KEY_NAME: ");
          System.out.println(parser.getString());
          break;
        case VALUE_NUMBER:
          System.out.print("VALUE_NUMBER: ");
          System.out.println(parser.getLong());
          break;
        case VALUE_STRING:
          System.out.print("VALUE_STRING: ");
          System.out.println(parser.getString());
          break;
        case VALUE_FALSE:
          System.out.println("VALUE_FALSE");
          break;
        case VALUE_TRUE:
          System.out.println("VALUE_TRUE");
          break;
        case VALUE_NULL:
          System.out.println("VALUE_NULL");
          break;
        default:
          System.out.println("something went wrong: " + event);
          break;
        }
      }
    }

  }
}
