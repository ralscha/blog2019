package ch.rasc.ratelimit;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.VerboseResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PerClientRateLimitInterceptor implements HandlerInterceptor {

  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

  private final Bucket freeBucket = Bucket.builder().addLimit(Bandwidth.builder()
      .capacity(10).refillIntervally(10, Duration.ofMinutes(1)).build()).build();

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) throws Exception {

    Bucket requestBucket;

    String apiKey = request.getHeader("X-api-key");
    if (apiKey != null && !apiKey.isBlank()) {
      if (apiKey.startsWith("1")) {
        requestBucket = this.buckets.computeIfAbsent(apiKey, key -> premiumBucket());
      }
      else {
        requestBucket = this.buckets.computeIfAbsent(apiKey, key -> standardBucket());
      }
    }
    else {
      requestBucket = this.freeBucket;
    }

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

  private static Bucket standardBucket() {
    return Bucket.builder().addLimit(Bandwidth.builder().capacity(50)
        .refillIntervally(50, Duration.ofMinutes(1)).build()).build();
  }

  private static Bucket premiumBucket() {
    return Bucket.builder().addLimit(Bandwidth.builder().capacity(100)
        .refillIntervally(100, Duration.ofMinutes(1)).build()).build();
  }

}
