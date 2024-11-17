package ch.rasc.ratelimit;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import com.hazelcast.core.HazelcastInstance;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.grid.hazelcast.HazelcastProxyManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PerClientHazelcastRateLimitInterceptor implements HandlerInterceptor {

  private final HazelcastProxyManager<String> proxyManager;

  public PerClientHazelcastRateLimitInterceptor(HazelcastInstance hzInstance) {
    this.proxyManager = new HazelcastProxyManager<>(
        hzInstance.getMap("per-client-bucket-map"));
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) throws Exception {

    String apiKey = request.getHeader("X-api-key");
    if (apiKey == null || apiKey.isBlank()) {
      apiKey = "free";
    }

    BucketConfiguration configuration;

    if (apiKey.startsWith("1")) {
      Bandwidth limit = Bandwidth.builder().capacity(100)
          .refillIntervally(100, Duration.ofMinutes(1)).build();
      configuration = BucketConfiguration.builder().addLimit(limit).build();
    }
    else if (!apiKey.equals("free")) {
      Bandwidth limit = Bandwidth.builder().capacity(50)
          .refillIntervally(50, Duration.ofMinutes(1)).build();
      configuration = BucketConfiguration.builder().addLimit(limit).build();
    }
    else {
      Bandwidth limit = Bandwidth.builder().capacity(10)
          .refillIntervally(10, Duration.ofMinutes(1)).build();
      configuration = BucketConfiguration.builder().addLimit(limit).build();
    }

    Bucket requestBucket = this.proxyManager.builder().build(apiKey, () -> configuration);

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

}
