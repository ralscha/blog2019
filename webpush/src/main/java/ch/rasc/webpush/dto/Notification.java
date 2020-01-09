package ch.rasc.webpush.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Notification {
  
  enum Direction {
    auto, ltr, rtl
  }

  private final String title;

  private Object data;

  private String badge;

  private String body;

  private Direction dir;

  private String icon;

  private String image;

  private String lang;

  private Boolean renotify;

  private Boolean requireInteraction;

  private Boolean silent;

  private String tag;

  private List<Integer> vibrate;

  private Long timestamp; // millis since 1970-01-01

  private List<NotificationAction> actions;

  public Notification(String title) {
    this.title = title;
  }

  public Object getData() {
    return this.data;
  }

  public void setData(Object data) {
    this.data = data;
  }

  public String getBadge() {
    return this.badge;
  }

  public void setBadge(String badge) {
    this.badge = badge;
  }

  public String getBody() {
    return this.body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public Direction getDir() {
    return this.dir;
  }

  public void setDir(Direction dir) {
    this.dir = dir;
  }

  public String getIcon() {
    return this.icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getImage() {
    return this.image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getLang() {
    return this.lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public Boolean getRenotify() {
    return this.renotify;
  }

  public void setRenotify(Boolean renotify) {
    this.renotify = renotify;
  }

  public Boolean getRequireInteraction() {
    return this.requireInteraction;
  }

  public void setRequireInteraction(Boolean requireInteraction) {
    this.requireInteraction = requireInteraction;
  }

  public Boolean getSilent() {
    return this.silent;
  }

  public void setSilent(Boolean silent) {
    this.silent = silent;
  }

  public String getTag() {
    return this.tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public List<Integer> getVibrate() {
    return this.vibrate;
  }

  public void setVibrate(List<Integer> vibrate) {
    this.vibrate = vibrate;
  }

  public Long getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public List<NotificationAction> getActions() {
    return this.actions;
  }

  public void setActions(List<NotificationAction> actions) {
    this.actions = actions;
  }

  public String getTitle() {
    return this.title;
  }

}
