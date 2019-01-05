package ch.rasc.bigint;

import java.math.BigInteger;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Payload {
  private long value1;

  private Long value2;

  @JsonSerialize(using = BigIntegerSerializer.class)
  @JsonDeserialize(using = BigIntegerDeserializer.class)
  private BigInteger prime;

  public Payload() {
    // default constructor
  }

  public Payload(long value1, Long value2, BigInteger prime) {
    this.value1 = value1;
    this.value2 = value2;
    this.prime = prime;
  }

  public long getValue1() {
    return this.value1;
  }

  public void setValue1(long value1) {
    this.value1 = value1;
  }

  @JsonSerialize(using = LongSerializer.class)
  public Long getValue2() {
    return this.value2;
  }

  @JsonDeserialize(using = LongDeserializer.class)
  public void setValue2(Long value2) {
    this.value2 = value2;
  }

  public BigInteger getPrime() {
    return this.prime;
  }

  public void setPrime(BigInteger prime) {
    this.prime = prime;
  }

  @Override
  public String toString() {
    return "Payload [value1=" + this.value1 + ", value2=" + this.value2 + ", prime="
        + this.prime + "]";
  }

}
