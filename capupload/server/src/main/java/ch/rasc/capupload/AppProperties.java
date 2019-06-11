package ch.rasc.capupload;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

  private String tusUploadDirectory;

  private String appUploadDirectory;

  public String getTusUploadDirectory() {
    return this.tusUploadDirectory;
  }

  public void setTusUploadDirectory(String tusUploadDirectory) {
    this.tusUploadDirectory = tusUploadDirectory;
  }

  public String getAppUploadDirectory() {
    return this.appUploadDirectory;
  }

  public void setAppUploadDirectory(String appUploadDirectory) {
    this.appUploadDirectory = appUploadDirectory;
  }

}
