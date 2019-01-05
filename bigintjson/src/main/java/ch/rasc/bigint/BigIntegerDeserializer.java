package ch.rasc.bigint;

import java.io.IOException;
import java.math.BigInteger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class BigIntegerDeserializer extends JsonDeserializer<BigInteger> {

  @Override
  public BigInteger deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    String value = jp.getText();
    if (value != null) {
      value = value.strip();
      if (value.length() > 0) {
        return new BigInteger(value.substring(0, value.length() - 1));
      }
    }
    return null;
  }

}