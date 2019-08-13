package ch.rasc.webpush.dto;

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
    final int prime = 31;
    int result = 1;
    result = prime * result + (this.endpoint == null ? 0 : this.endpoint.hashCode());
    result = prime * result
        + (this.expirationTime == null ? 0 : this.expirationTime.hashCode());
    result = prime * result + (this.keys == null ? 0 : this.keys.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Subscription other = (Subscription) obj;
    if (this.endpoint == null) {
      if (other.endpoint != null) {
        return false;
      }
    }
    else if (!this.endpoint.equals(other.endpoint)) {
      return false;
    }
    if (this.expirationTime == null) {
      if (other.expirationTime != null) {
        return false;
      }
    }
    else if (!this.expirationTime.equals(other.expirationTime)) {
      return false;
    }
    if (this.keys == null) {
      if (other.keys != null) {
        return false;
      }
    }
    else if (!this.keys.equals(other.keys)) {
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
