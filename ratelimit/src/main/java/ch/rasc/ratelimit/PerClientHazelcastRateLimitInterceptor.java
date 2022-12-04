package ch.rasc.ratelimit;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import com.hazelcast.core.HazelcastInstance;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import io.github.bucket4j.grid.hazelcast.HazelcastProxyManager;

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
      configuration = BucketConfiguration.builder()
          .addLimit(Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1))))
          .build();
    }
    else if (!apiKey.equals("free")) {
      configuration = BucketConfiguration.builder()
          .addLimit(Bandwidth.classic(50, Refill.intervally(50, Duration.ofMinutes(1))))
          .build();
    }
    else {
      configuration = BucketConfiguration.builder()
          .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1))))
          .build();
    }

    Bucket requestBucket = this.proxyManager.builder().build(apiKey, configuration);

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
