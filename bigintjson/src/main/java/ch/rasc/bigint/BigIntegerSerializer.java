package ch.rasc.bigint;

import java.math.BigInteger;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class BigIntegerSerializer extends ValueSerializer<BigInteger> {

  @Override
  public void serialize(BigInteger value, JsonGenerator gen, SerializationContext context) {
    gen.writeString(value.toString() + "n");
  }

}