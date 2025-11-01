package ch.rasc.envers;

import java.util.Date;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "REVINFO")
@RevisionEntity(CustomRevisionEntityListener.class)
public class CustomRevisionEntity {

  @Id
  @GeneratedValue
  @RevisionNumber
  private int id;

  @RevisionTimestamp
  private long timestamp;

  private String username;

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public long getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  @Transient
  public Date getRevisionDate() {
    return new Date(this.timestamp);
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}