package ch.rasc.bigint;

import java.math.BigInteger;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public class BigIntegerDeserializer extends ValueDeserializer<BigInteger> {

  @Override
  public BigInteger deserialize(JsonParser jp, DeserializationContext ctxt) {
    String value = jp.getString();
    if (value != null) {
      value = value.strip();
      if (value.length() > 0) {
        return new BigInteger(value.substring(0, value.length() - 1));
      }
    }
    return null;
  }

}