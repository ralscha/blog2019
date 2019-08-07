package db.migration;

import java.math.BigDecimal;
import java.util.Date;

import com.univocity.parsers.annotations.Format;
import com.univocity.parsers.annotations.Parsed;

public class EarthquakeImport {

  @Parsed(field = "id")
  private String id;

  @Parsed(field = "latitude")
  private BigDecimal latitude;

  @Parsed(field = "longitude")
  private BigDecimal longitude;

  @Parsed(field = "time")
  @Format(formats = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date time;

  @Parsed(field = "depth")
  private BigDecimal depth;

  @Parsed(field = "mag")
  private BigDecimal mag;

  @Parsed(field = "place")
  private String place;

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public BigDecimal getLatitude() {
    return this.latitude;
  }

  public void setLatitude(BigDecimal latitude) {
    this.latitude = latitude;
  }

  public BigDecimal getLongitude() {
    return this.longitude;
  }

  public void setLongitude(BigDecimal longitude) {
    this.longitude = longitude;
  }

  public Date getTime() {
    return this.time;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public BigDecimal getDepth() {
    return this.depth;
  }

  public void setDepth(BigDecimal depth) {
    this.depth = depth;
  }

  public BigDecimal getMag() {
    return this.mag;
  }

  public void setMag(BigDecimal mag) {
    this.mag = mag;
  }

  public String getPlace() {
    return this.place;
  }

  public void setPlace(String place) {
    this.place = place;
  }

  @Override
  public String toString() {
    return "Earthquake [id=" + this.id + ", latitude=" + this.latitude + ", longitude="
        + this.longitude + ", time=" + this.time + ", depth=" + this.depth + ", mag="
        + this.mag + ", place=" + this.place + "]";
  }

}
