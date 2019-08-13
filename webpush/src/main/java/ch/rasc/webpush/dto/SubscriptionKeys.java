package ch.rasc.webpush.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SubscriptionKeys {
  private final String p256dh;

  private final String auth;

  @JsonCreator
  public SubscriptionKeys(@JsonProperty("p256dh") String p256dh,
      @JsonProperty("auth") String auth) {
    this.p256dh = p256dh;
    this.auth = auth;
  }

  public String getP256dh() {
    return this.p256dh;
  }

  public String getAuth() {
    return this.auth;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (this.auth == null ? 0 : this.auth.hashCode());
    result = prime * result + (this.p256dh == null ? 0 : this.p256dh.hashCode());
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
    SubscriptionKeys other = (SubscriptionKeys) obj;
    if (this.auth == null) {
      if (other.auth != null) {
        return false;
      }
    }
    else if (!this.auth.equals(other.auth)) {
      return false;
    }
    if (this.p256dh == null) {
      if (other.p256dh != null) {
        return false;
      }
    }
    else if (!this.p256dh.equals(other.p256dh)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "SubscriptionKeys [p256dh=" + this.p256dh + ", auth=" + this.auth + "]";
  }

}