package ch.rasc.ratelimit;

import java.time.Duration;
import java.util.function.Supplier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.grid.hazelcast.Bucket4jHazelcast;
import io.github.bucket4j.grid.hazelcast.HazelcastProxyManager;

@SpringBootApplication
public class Application implements WebMvcConfigurer {

  private final HazelcastInstance hazelcastInstance;

  public Application(HazelcastInstance hazelcastInstance) {
    this.hazelcastInstance = hazelcastInstance;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    Bandwidth limit = Bandwidth.builder().capacity(10)
        .refillGreedy(10, Duration.ofMinutes(1)).build();
    Bucket bucket = Bucket.builder().addLimit(limit).build();
    registry.addInterceptor(new RateLimitInterceptor(bucket, 1)).addPathPatterns("/last");

    Bandwidth placeLimit = Bandwidth.builder().capacity(3)
        .refillIntervally(3, Duration.ofMinutes(1))
        .build();
    Bucket hazelcastBucket = clusteredBucket("bucket-map", "place-rate-limit",
        () -> BucketConfiguration.builder().addLimit(placeLimit).build());

    registry.addInterceptor(new RateLimitInterceptor(hazelcastBucket, 1))
        .addPathPatterns("/place/*");

    registry.addInterceptor(new PerClientRateLimitInterceptor())
        .addPathPatterns("/depth/**");

    registry.addInterceptor(
        new PerClientHazelcastRateLimitInterceptor(this.hazelcastInstance))
        .addPathPatterns("/mag/**");

  }

  private Bucket clusteredBucket(String mapName, String bucketKey,
      Supplier<BucketConfiguration> configurationSupplier) {
    IMap<String, byte[]> map = this.hazelcastInstance.getMap(mapName);
    HazelcastProxyManager<String> proxyManager = Bucket4jHazelcast
        .entryProcessorBasedBuilder(map).build();
    return proxyManager.getProxy(bucketKey, configurationSupplier);
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
