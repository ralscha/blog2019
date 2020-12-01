package db.migration;

import static ch.rasc.ratelimit.db.tables.Earthquake.EARTHQUAKE;
import static org.jooq.impl.DSL.using;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.ZoneId;
import java.util.List;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

public class V0002__initial_import extends BaseJavaMigration {

  @Override
  public void migrate(Context context) throws Exception {

    List<EarthquakeImport> earthquakes = readEarthquakes();

    DSLContext dsl = using(context.getConnection());

    dsl.transaction(txConf -> {
      var txdsl = DSL.using(txConf);

      try (var insert = txdsl.insertInto(EARTHQUAKE, EARTHQUAKE.EARTHQUAKE_ID,
          EARTHQUAKE.LATITUDE, EARTHQUAKE.LONGITUDE, EARTHQUAKE.MAG, EARTHQUAKE.DEPTH,
          EARTHQUAKE.PLACE, EARTHQUAKE.TIME)) {

        for (EarthquakeImport earthquake : earthquakes) {
          if (earthquake.getMag() != null) {

            insert.values(earthquake.getId(), earthquake.getLatitude(),
                earthquake.getLongitude(), earthquake.getMag(), earthquake.getDepth(),
                earthquake.getPlace(), earthquake.getTime().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime());
          }

        }
        insert.execute();
      }

    });
  }

  private static List<EarthquakeImport> readEarthquakes()
      throws IOException, InterruptedException {
    String url = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.csv";

    HttpClient httpClient = HttpClient.newBuilder().build();
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

    HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

    BeanListProcessor<EarthquakeImport> rowProcessor = new BeanListProcessor<>(
        EarthquakeImport.class);

    CsvParserSettings settings = new CsvParserSettings();
    settings.setHeaderExtractionEnabled(true);
    settings.setLineSeparatorDetectionEnabled(true);
    settings.setProcessor(rowProcessor);
    CsvParser parser = new CsvParser(settings);
    parser.parse(new StringReader(response.body()));

    return rowProcessor.getBeans();
  }
}