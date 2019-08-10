package ch.rasc.ky;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Value {
  private final String value;

  @JsonCreator
  public Value(@JsonProperty("value") String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

}
