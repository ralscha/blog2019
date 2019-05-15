package db.migration;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.using;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class V0002__initial_import extends BaseJavaMigration {

  @Override
  public void migrate(Context context) throws Exception {

    PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    try (DSLContext dsl = using(context.getConnection())) {

      dsl.transaction(txConf -> {
        try (var txdsl = DSL.using(txConf)) {
          txdsl
              .insertInto(table("APP_USER"), field("ID"), field("EMAIL"),
                  field("PASSWORD_HASH"), field("AUTHORITY"), field("ENABLED"))
              .values(1, "admin@test.com", pe.encode("admin"), "ADMIN", true)
              .values(2, "user@test.com", pe.encode("user"), "USER", true).execute();

        }
      });
    }

  }
}