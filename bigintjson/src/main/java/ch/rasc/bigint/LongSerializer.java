package ch.rasc.bigint;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class LongSerializer extends JsonSerializer<Long> {

  @Override
  public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    gen.writeString(value.toString() + "n");
  }

}