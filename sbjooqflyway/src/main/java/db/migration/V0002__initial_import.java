package db.migration;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.using;

import java.io.InputStream;
import java.sql.Date;
import java.util.List;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.core.io.ClassPathResource;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

public class V0002__initial_import extends BaseJavaMigration {

  @Override
  public void migrate(Context context) throws Exception {
    ClassPathResource cpr = new ClassPathResource("employees.csv");
    try (InputStream is = cpr.getInputStream()) {

      CsvParserSettings settings = new CsvParserSettings();
      settings.getFormat().setDelimiter(',');
      settings.getFormat().setQuote('"');
      CsvParser parser = new CsvParser(settings);

      List<String[]> rows = parser.parseAll(is);

      DSLContext dsl = using(context.getConnection());
      for (String[] row : rows) {
        dsl.insertInto(table("employee"), field("user_name"), field("birth_date"),
            field("first_name"), field("last_name"), field("gender"), field("hire_date"))
            .values(row[0], DSL.cast(row[1], Date.class), row[2], row[3], row[4],
                DSL.cast(row[5], Date.class))
            .execute();
      }
    }
  }
}