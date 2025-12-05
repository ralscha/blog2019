package ch.rasc.bigint;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public class LongDeserializer extends ValueDeserializer<Long> {

  @Override
  public Long deserialize(JsonParser jp, DeserializationContext ctxt) {
    String value = jp.getString();
    if (value != null) {
      value = value.strip();
      if (value.length() > 0) {
        return Long.valueOf(value.substring(0, value.length() - 1));
      }
    }
    return null;
  }

}