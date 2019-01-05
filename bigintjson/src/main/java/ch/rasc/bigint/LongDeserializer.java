package ch.rasc.bigint;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class LongDeserializer extends JsonDeserializer<Long> {

  @Override
  public Long deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    String value = jp.getText();
    if (value != null) {
      value = value.strip();
      if (value.length() > 0) {
        return Long.valueOf(value.substring(0, value.length() - 1));
      }
    }
    return null;
  }

}