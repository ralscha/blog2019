package ch.rasc.ratelimit;

import java.time.Duration;
import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import com.hazelcast.core.HazelcastInstance;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.VerboseResult;
import io.github.bucket4j.grid.hazelcast.Bucket4jHazelcast;
import io.github.bucket4j.grid.hazelcast.HazelcastProxyManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PerClientHazelcastRateLimitInterceptor implements HandlerInterceptor {

  private final HazelcastProxyManager<String> proxyManager;

  public PerClientHazelcastRateLimitInterceptor(HazelcastInstance hzInstance) {
    this.proxyManager = Bucket4jHazelcast
        .entryProcessorBasedBuilder(hzInstance.<String, byte[]>getMap(
            "per-client-bucket-map"))
      .build();
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

    Bucket requestBucket = this.proxyManager.getProxy(apiKey, () -> configuration);

    VerboseResult<ConsumptionProbe> verboseResult = requestBucket.asVerbose()
        .tryConsumeAndReturnRemaining(1);
    ConsumptionProbe probe = verboseResult.getValue();
    long limit = Arrays.stream(verboseResult.getConfiguration().getBandwidths())
        .mapToLong(Bandwidth::getCapacity).max().orElseThrow();
    long resetSeconds = nanosToSeconds(
        verboseResult.getDiagnostics().calculateFullRefillingTime());

    if (probe.isConsumed()) {
      response.setHeader("RateLimit-Limit", Long.toString(limit));
      response.setHeader("RateLimit-Remaining",
          Long.toString(verboseResult.getDiagnostics().getAvailableTokens()));
      response.setHeader("RateLimit-Reset", Long.toString(resetSeconds));
      return true;
    }

    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // 429
    response.setHeader("RateLimit-Limit", Long.toString(limit));
    response.setHeader("RateLimit-Remaining",
        Long.toString(verboseResult.getDiagnostics().getAvailableTokens()));
    response.setHeader("RateLimit-Reset", Long.toString(resetSeconds));
    response.setHeader("Retry-After",
        Long.toString(nanosToSeconds(probe.getNanosToWaitForRefill())));

    return false;
  }

  private static long nanosToSeconds(long nanos) {
    return nanos <= 0 ? 0 : Math.ceilDiv(nanos, 1_000_000_000L);
  }

}
