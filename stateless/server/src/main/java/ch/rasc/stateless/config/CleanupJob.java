package ch.rasc.stateless.config;

import static ch.rasc.stateless.db.tables.AppSession.APP_SESSION;

import java.time.LocalDateTime;

import org.jooq.DSLContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CleanupJob {

  private final DSLContext dsl;

  public CleanupJob(DSLContext dsl) {
    this.dsl = dsl;
  }

  @Scheduled(cron = "0 0 5 * * *")
  public void doCleanup() {
    this.dsl.delete(APP_SESSION).where(APP_SESSION.VALID_UNTIL.le(LocalDateTime.now()))
        .execute();
  }

}
