package db.migration;

import static ch.rasc.totp.db.tables.AppUser.APP_USER;
import static org.jooq.impl.DSL.using;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class V0002__initial_import extends BaseJavaMigration {

  @Override
  public void migrate(Context context) throws Exception {

    try (DSLContext dsl = using(context.getConnection())) {

      PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
      dsl.insertInto(APP_USER, APP_USER.USERNAME, APP_USER.PASSWORD_HASH, APP_USER.SECRET,
          APP_USER.ENABLED).values("admin", pe.encode("admin"), "W4AU5VIXXCPZ3S6T", true)
          .values("user", pe.encode("user"), "LRVLAZ4WVFOU3JBF", true)
          .values("lazy", pe.encode("lazy"), null, true).execute();
    }

  }
}