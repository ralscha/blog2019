package ch.rasc.ratelimit;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
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

  private static Bucket standardBucket() {
    return Bucket.builder().addLimit(Bandwidth.builder().capacity(50)
        .refillIntervally(50, Duration.ofMinutes(1)).build()).build();
  }

  private static Bucket premiumBucket() {
    return Bucket.builder().addLimit(Bandwidth.builder().capacity(100)
        .refillIntervally(100, Duration.ofMinutes(1)).build()).build();
  }

}
