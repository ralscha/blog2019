package ch.rasc.envers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
  private static final String JDBC_URL = "jdbc:h2:./db/test";

  private static final String JDBC_USER = "sa";

  private static final String JDBC_PASSWORD = "";

  private static EntityManagerFactory emFactory;

  public static EntityManagerFactory getEntityManagerFactory() {
    if (emFactory == null) {
      emFactory = Persistence.createEntityManagerFactory("PERSISTENCE");
    }
    return emFactory;
  }

  public static void resetDemoDatabase() {
    shutdown();

    try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER,
        JDBC_PASSWORD);
        var statement = connection.createStatement()) {
      statement.execute("DROP ALL OBJECTS");
    }
    catch (SQLException e) {
      throw new IllegalStateException("Unable to reset the demo database", e);
    }
  }

  public static void shutdown() {
    if (emFactory != null) {
      emFactory.close();
      emFactory = null;
    }
  }
}