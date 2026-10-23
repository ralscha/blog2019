package ch.rasc.bigint;

import java.math.BigInteger;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public class BigIntegerDeserializer extends ValueDeserializer<BigInteger> {

  @Override
  public BigInteger deserialize(JsonParser jp, DeserializationContext ctxt) {
    String value = jp.getString();
    if (value == null || !value.matches("-?[0-9]+n")) {
      throw ctxt.weirdStringException(value, BigInteger.class,
          "Expected a decimal integer followed by n");
    }
    return new BigInteger(value.substring(0, value.length() - 1));
  }

}
