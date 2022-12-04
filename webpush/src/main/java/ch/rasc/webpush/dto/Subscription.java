package ch.rasc.webpush.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Subscription {
  private final String endpoint;

  private final Long expirationTime;

  public final SubscriptionKeys keys;

  @JsonCreator
  public Subscription(@JsonProperty("endpoint") String endpoint,
      @JsonProperty("expirationTime") Long expirationTime,
      @JsonProperty("keys") SubscriptionKeys keys) {
    this.endpoint = endpoint;
    this.expirationTime = expirationTime;
    this.keys = keys;
  }

  public String getEndpoint() {
    return this.endpoint;
  }

  public Long getExpirationTime() {
    return this.expirationTime;
  }

  public SubscriptionKeys getKeys() {
    return this.keys;
  }

  @Override
  public int hashCode() {
    return Objects.hash(endpoint, expirationTime, keys);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    Subscription other = (Subscription) obj;
    if (!Objects.equals(this.endpoint, other.endpoint)) {
      return false;
    }
    if (!Objects.equals(this.expirationTime, other.expirationTime)) {
      return false;
    }
    if (!Objects.equals(this.keys, other.keys)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Subscription [endpoint=" + this.endpoint + ", expirationTime="
        + this.expirationTime + ", keys=" + this.keys + "]";
  }

}
