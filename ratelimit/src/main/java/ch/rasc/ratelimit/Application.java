package ch.rasc.ratelimit;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import io.github.bucket4j.grid.GridBucketState;
import io.github.bucket4j.grid.RecoveryStrategy;
import io.github.bucket4j.grid.hazelcast.Hazelcast;

@SpringBootApplication
public class Application implements WebMvcConfigurer {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    Refill refill = Refill.greedy(10, Duration.ofMinutes(1));
    Bandwidth limit = Bandwidth.classic(10, refill).withInitialTokens(1);
    Bucket bucket = Bucket4j.builder().addLimit(limit).build();
    registry.addInterceptor(new RateLimitInterceptor(bucket, 1)).addPathPatterns("/last");

    refill = Refill.intervally(3, Duration.ofMinutes(1));
    limit = Bandwidth.classic(3, refill);
    bucket = Bucket4j.builder().addLimit(limit).build();
    registry.addInterceptor(new RateLimitInterceptor(bucket, 1))
        .addPathPatterns("/place/*");

    IMap<String, GridBucketState> map = this.hzInstance.getMap("bucket-map");
    bucket = Bucket4j.extension(Hazelcast.class).builder().addLimit(limit).build(map,
        "rate-limit", RecoveryStrategy.RECONSTRUCT);
    registry.addInterceptor(new RateLimitInterceptor(bucket, 1))
        .addPathPatterns("/place/*");

    registry.addInterceptor(new PerClientRateLimitInterceptor())
        .addPathPatterns("/depth/**");

    registry.addInterceptor(new PerClientHazelcastRateLimitInterceptor(this.hzInstance))
        .addPathPatterns("/mag/**");

  }

  @Autowired
  private HazelcastInstance hzInstance;

  @Bean
  public Config hazelCastConfig() {
    Config config = new Config();
    config.setInstanceName("my-hazelcast-instance");
    return config;
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
