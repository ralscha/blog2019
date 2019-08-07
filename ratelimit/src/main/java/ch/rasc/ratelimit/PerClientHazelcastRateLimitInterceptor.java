package ch.rasc.ratelimit;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import com.hazelcast.core.HazelcastInstance;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import io.github.bucket4j.grid.ProxyManager;
import io.github.bucket4j.grid.hazelcast.Hazelcast;

public class PerClientHazelcastRateLimitInterceptor implements HandlerInterceptor {

  private final ProxyManager<String> buckets;

  public PerClientHazelcastRateLimitInterceptor(HazelcastInstance hzInstance) {
    this.buckets = Bucket4j.extension(Hazelcast.class)
        .proxyManagerForMap(hzInstance.getMap("per-client-bucket-map"));
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) throws Exception {

    String apiKey = request.getHeader("X-api-key");
    if (apiKey == null || apiKey.isBlank()) {
      apiKey = "free";
    }
    Bucket requestBucket = this.buckets.getProxy(apiKey, getConfigSupplier(apiKey));

    ConsumptionProbe probe = requestBucket.tryConsumeAndReturnRemaining(1);
    if (probe.isConsumed()) {
      response.addHeader("X-Rate-Limit-Remaining",
          Long.toString(probe.getRemainingTokens()));
      return true;
    }

    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // 429
    response.addHeader("X-Rate-Limit-Retry-After-Milliseconds",
        Long.toString(TimeUnit.NANOSECONDS.toMillis(probe.getNanosToWaitForRefill())));

    return false;
  }

  private static Supplier<BucketConfiguration> getConfigSupplier(String apiKey) {
    return () -> {
      if (apiKey.startsWith("1")) {
        return Bucket4j.configurationBuilder()
            .addLimit(
                Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1))))
            .build();
      }
      else if (!apiKey.equals("free")) {
        return Bucket4j.configurationBuilder()
            .addLimit(Bandwidth.classic(50, Refill.intervally(50, Duration.ofMinutes(1))))
            .build();
      }
      return Bucket4j.configurationBuilder()
          .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1))))
          .build();

    };
  }

}
