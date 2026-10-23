package ch.rasc.bigint;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public class LongDeserializer extends ValueDeserializer<Long> {

  @Override
  public Long deserialize(JsonParser jp, DeserializationContext ctxt) {
    String value = jp.getString();
    if (value == null || !value.matches("-?[0-9]+n")) {
      throw ctxt.weirdStringException(value, Long.class,
          "Expected a decimal integer followed by n");
    }
    try {
      return Long.valueOf(value.substring(0, value.length() - 1));
    }
    catch (NumberFormatException e) {
      throw ctxt.weirdStringException(value, Long.class,
          "Integer is outside the signed 64-bit range");
    }
  }

}
