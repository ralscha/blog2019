package ch.rasc.envers;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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