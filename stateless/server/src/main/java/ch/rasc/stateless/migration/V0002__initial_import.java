package ch.rasc.stateless.migration;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.using;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class V0002__initial_import extends BaseJavaMigration {

  private final PasswordEncoder passwordEncoder;

  public V0002__initial_import(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void migrate(Context context) throws Exception {

    DSLContext dsl = using(context.getConnection());
    dsl.transaction(txConf -> {
      var txdsl = DSL.using(txConf);
      txdsl
          .insertInto(table("APP_USER"), field("ID"), field("EMAIL"),
              field("PASSWORD_HASH"), field("AUTHORITY"), field("ENABLED"))
          .values(1, "admin@test.com", this.passwordEncoder.encode("admin"), "ADMIN",
              true)
          .values(2, "user@test.com", this.passwordEncoder.encode("user"), "USER", true)
          .execute();

    });
  }
}