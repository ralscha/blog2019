package ch.rasc.ratelimit;

import java.util.Arrays;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.VerboseResult;

public class RateLimitInterceptor implements HandlerInterceptor {

  private final Bucket bucket;

  private final int numTokens;

  public RateLimitInterceptor(Bucket bucket, int numTokens) {
    this.bucket = bucket;
    this.numTokens = numTokens;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) throws Exception {

    VerboseResult<ConsumptionProbe> verboseResult = this.bucket.asVerbose()
        .tryConsumeAndReturnRemaining(this.numTokens);
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
