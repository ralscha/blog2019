package ch.rasc.envers;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
  private static EntityManagerFactory emFactory;

  public static EntityManagerFactory getEntityManagerFactory() {
    if (emFactory == null) {
      emFactory = Persistence.createEntityManagerFactory("PERSISTENCE");
    }
    return emFactory;
  }

  public static void shutdown() {
    if (emFactory != null) {
      emFactory.close();
    }
  }
}