package ch.rasc.ratelimit;

import static ch.rasc.ratelimit.db.tables.Earthquake.EARTHQUAKE;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jooq.DSLContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import ch.rasc.ratelimit.db.tables.pojos.Earthquake;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;

@RestController
public class EarthquakeController {

  private final DSLContext dsl;

  private final Bucket bucket;

  public EarthquakeController(DSLContext dsl) {
    this.dsl = dsl;

    long capacity = 10;
    Bandwidth limit = Bandwidth.builder().capacity(capacity)
        .refillGreedy(capacity, Duration.ofMinutes(1)).build();
    this.bucket = Bucket.builder().addLimit(limit).build();
  }

  @GetMapping("/top1")
  public ResponseEntity<Earthquake> getTop1() {
    if (this.bucket.tryConsume(1)) {
      Earthquake body = this.dsl.selectFrom(EARTHQUAKE).orderBy(EARTHQUAKE.MAG.desc())
          .limit(1).fetchOneInto(Earthquake.class);
      return ResponseEntity.ok().body(body);
    }

    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
  }

  @GetMapping("/top/{top}")
  public ResponseEntity<List<Earthquake>> getTopOrderByMag(@PathVariable("top") int top) {
    ConsumptionProbe probe = this.bucket.tryConsumeAndReturnRemaining(1);
    if (probe.isConsumed()) {
      List<Earthquake> body = this.dsl.selectFrom(EARTHQUAKE)
          .orderBy(EARTHQUAKE.MAG.desc()).limit(top).fetchInto(Earthquake.class);
      return ResponseEntity.ok()
          .header("X-Rate-Limit-Remaining", Long.toString(probe.getRemainingTokens()))
          .body(body);
    }

    // X-Rate-Limit-Retry-After-Seconds
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
        .header("X-Rate-Limit-Retry-After-Milliseconds",
            Long.toString(TimeUnit.NANOSECONDS.toMillis(probe.getNanosToWaitForRefill())))
        .build();
  }

  @GetMapping("/last")
  public Earthquake getLast() {
    return this.dsl.selectFrom(EARTHQUAKE).orderBy(EARTHQUAKE.TIME.desc()).limit(1)
        .fetchOneInto(Earthquake.class);
  }

  @GetMapping("/place/{place}")
  public List<Earthquake> getPlace(@PathVariable("place") String place) {
    return this.dsl.selectFrom(EARTHQUAKE).where(EARTHQUAKE.PLACE.endsWith(place))
        .fetchInto(Earthquake.class);
  }

  @GetMapping("/depth/{from}/{to}")
  public List<Earthquake> getPlace(@PathVariable("from") BigDecimal fromDepth,
      @PathVariable("to") BigDecimal toDepth) {
    return this.dsl.selectFrom(EARTHQUAKE)
        .where(EARTHQUAKE.DEPTH.between(fromDepth, toDepth)).fetchInto(Earthquake.class);
  }

  @GetMapping("/mag/{from}/{to}")
  public List<Earthquake> getMag(@PathVariable("from") BigDecimal fromMag,
      @PathVariable("to") BigDecimal toMag) {
    return this.dsl.selectFrom(EARTHQUAKE).where(EARTHQUAKE.MAG.between(fromMag, toMag))
        .fetchInto(Earthquake.class);
  }
}
