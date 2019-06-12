package ch.rasc.buildinfo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@CrossOrigin
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Autowired
  private BuildProperties buildProperties;

  @GetMapping("/build-info")
  public BuildProperties buildInfo() {
    return this.buildProperties;
  }

  @Autowired
  private GitProperties gitProperties;

  @GetMapping("/git-info")
  public GitProperties gitInfo() {
    return this.gitProperties;
  }

  @Autowired
  private Environment environment;

  @GetMapping("/profile-info")
  public Map<String, Object> profileInfo() {
    return Map.of("activeProfiles",
        String.join(",", this.environment.getActiveProfiles()), "defaultProfiles",
        String.join(",", this.environment.getDefaultProfiles()));
  }

}
