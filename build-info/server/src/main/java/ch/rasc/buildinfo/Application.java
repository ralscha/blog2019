package ch.rasc.buildinfo;

import java.util.Map;

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

  private final BuildProperties buildProperties;

  private final GitProperties gitProperties;

  private final Environment environment;

  public Application(BuildProperties buildProperties, GitProperties gitProperties,
      Environment environment) {
    this.buildProperties = buildProperties;
    this.gitProperties = gitProperties;
    this.environment = environment;
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @GetMapping("/build-info")
  public BuildProperties buildInfo() {
    return this.buildProperties;
  }

  @GetMapping("/git-info")
  public GitProperties gitInfo() {
    return this.gitProperties;
  }

  @GetMapping("/profile-info")
  public Map<String, Object> profileInfo() {
    return Map.of("activeProfiles",
        String.join(",", this.environment.getActiveProfiles()), "defaultProfiles",
        String.join(",", this.environment.getDefaultProfiles()));
  }

}
