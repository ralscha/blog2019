package ch.rasc.bigint;

import java.math.BigInteger;

import com.fasterxml.jackson.databind.module.SimpleModule;

// @Component
public class CustomModule extends SimpleModule {

  private static final long serialVersionUID = 1L;

  public CustomModule() {
    this.addSerializer(BigInteger.class, new BigIntegerSerializer());
    this.addSerializer(Long.class, new LongSerializer());
    this.addSerializer(long.class, new LongSerializer());

    this.addDeserializer(BigInteger.class, new BigIntegerDeserializer());
    this.addDeserializer(Long.class, new LongDeserializer());
    this.addDeserializer(long.class, new LongDeserializer());

  }
}