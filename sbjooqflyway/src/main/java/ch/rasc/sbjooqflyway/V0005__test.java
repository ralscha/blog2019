package ch.rasc.sbjooqflyway;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.stereotype.Component;

@Component
public class V0005__test extends BaseJavaMigration {

  @Override
  public void migrate(Context context) throws Exception {
    System.out.println("running migration 5");
  }
}