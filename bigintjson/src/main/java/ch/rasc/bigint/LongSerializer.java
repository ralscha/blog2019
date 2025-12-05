package ch.rasc.bigint;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class LongSerializer extends ValueSerializer<Long> {

  @Override
  public void serialize(Long value, JsonGenerator gen, SerializationContext context) {
    gen.writeString(value.toString() + "n");
  }

}