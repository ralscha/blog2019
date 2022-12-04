package ch.rasc.webpush.dto;

import java.util.Objects;

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
    return Objects.hash(auth, p256dh);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    SubscriptionKeys other = (SubscriptionKeys) obj;
    if (!Objects.equals(this.auth, other.auth)) {
      return false;
    }
    if (!Objects.equals(this.p256dh, other.p256dh)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "SubscriptionKeys [p256dh=" + this.p256dh + ", auth=" + this.auth + "]";
  }

}