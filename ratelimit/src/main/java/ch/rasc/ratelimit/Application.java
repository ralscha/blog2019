package ch.rasc.ratelimit;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.grid.hazelcast.HazelcastProxyManager;

@SpringBootApplication
public class Application implements WebMvcConfigurer {

  @Autowired
  private HazelcastInstance hzInstance;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    Bandwidth limit = Bandwidth.builder().capacity(10)
        .refillGreedy(10, Duration.ofMinutes(1)).build();
    Bucket bucket = Bucket.builder().addLimit(limit).build();
    registry.addInterceptor(new RateLimitInterceptor(bucket, 1)).addPathPatterns("/last");

    limit = Bandwidth.builder().capacity(3).refillIntervally(3, Duration.ofMinutes(1))
        .build();
    bucket = Bucket.builder().addLimit(limit).build();
    registry.addInterceptor(new RateLimitInterceptor(bucket, 1))
        .addPathPatterns("/place/*");

    BucketConfiguration configuration = BucketConfiguration.builder().addLimit(limit)
        .build();
    IMap<String, byte[]> map = this.hzInstance.getMap("bucket-map");
    HazelcastProxyManager<String> proxyManager = new HazelcastProxyManager<>(map);
    Bucket hazelcastBucket = proxyManager.builder().build("rate-limit",
        () -> configuration);

    registry.addInterceptor(new RateLimitInterceptor(hazelcastBucket, 1))
        .addPathPatterns("/place/*");

    registry.addInterceptor(new PerClientRateLimitInterceptor())
        .addPathPatterns("/depth/**");

    registry.addInterceptor(new PerClientHazelcastRateLimitInterceptor(this.hzInstance))
        .addPathPatterns("/mag/**");

  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
