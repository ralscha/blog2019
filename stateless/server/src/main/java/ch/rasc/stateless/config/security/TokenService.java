package ch.rasc.stateless.config.security;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import ch.rasc.stateless.config.AppProperties;

@Service
public class TokenService {

  private static final AtomicLong COUNTER = new AtomicLong();

  private static final long RESEED = 100000L;

  private final SecureRandom random;

  private final String instanceNo;

  public TokenService(AppProperties appProperties) {
    this.random = new SecureRandom();
    this.instanceNo = appProperties.getInstanceNo();
  }

  public String createToken() {
    synchronized (this.random) {
      long r0 = this.random.nextLong();

      // random chance to reseed
      if (r0 % RESEED == 1L) {
        this.random.setSeed(this.random.generateSeed(8));
      }

      long r1 = this.random.nextLong();
      return this.instanceNo + Long.toString(r0, 36) + Long.toString(r1, 36)
          + COUNTER.getAndIncrement();
    }
  }

}
