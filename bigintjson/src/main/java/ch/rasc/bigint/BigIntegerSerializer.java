package ch.rasc.bigint;

import java.io.IOException;
import java.math.BigInteger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class BigIntegerSerializer extends JsonSerializer<BigInteger> {

  @Override
  public void serialize(BigInteger value, JsonGenerator gen,
      SerializerProvider serializers) throws IOException {
    gen.writeString(value.toString() + "n");
  }

}