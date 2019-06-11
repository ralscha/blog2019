package ch.rasc.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import me.desair.tus.server.TusFileUploadService;

@SpringBootApplication
@EnableScheduling
public class Application {

  public static final Logger logger = LoggerFactory.getLogger("ch.rasc.upload");

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public TusFileUploadService tusFileUploadService(AppProperties appProperties) {
    return new TusFileUploadService().withStoragePath(appProperties.getTusUploadDirectory())
        .withUploadURI("/upload");
  }
}
