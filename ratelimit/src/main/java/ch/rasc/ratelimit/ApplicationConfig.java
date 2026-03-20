package ch.rasc.ratelimit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;

@Configuration
public class ApplicationConfig {

  @Bean
  public Config hazelcastConfig() {
    Config config = new Config();
    config.setInstanceName("my-hazelcast-instance");
    return config;
  }

}
