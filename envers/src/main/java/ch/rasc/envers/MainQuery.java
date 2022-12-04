package ch.rasc.envers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;

public class MainQuery {

  @SuppressWarnings("unchecked")
  public static void main(String[] args) {

    // Queries
    EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
    AuditReader reader = AuditReaderFactory.get(em);

    System.out.println("getRevisions, getRevisionDate, findRevision, find");
    List<Number> revisions = reader.getRevisions(Company.class, 1);
    for (Number rev : revisions) {
      System.out.println(rev);
      Date revisionDate = reader.getRevisionDate(rev);
      System.out.println(revisionDate);

      CustomRevisionEntity revision = reader.findRevision(CustomRevisionEntity.class,
          rev);
      String username = revision.getUsername();
      System.out.println(username);

      Company comp = reader.find(Company.class, 1, rev);
      String name = comp.getName();
      String street = comp.getStreet();
      System.out.println(name);
      System.out.println(street);
      System.out.println("------------------------------------------------");
    }

    System.out.println("===========================================");
    System.out.println("find revision 5");
    Company comp = reader.find(Company.class, 1, 5);
    String name = comp.getName();
    System.out.println(name); // output: EEE Corp

    System.out.println("===========================================");
    System.out.println("getRevisionNumberForDate");
    Calendar cal = Calendar.getInstance();
    Number revNumber = reader.getRevisionNumberForDate(cal.getTime());
    System.out.println(revNumber); // output: 5

    System.out.println("===========================================");
    System.out.println("forEntitiesAtRevision");
    AuditQuery query = reader.createQuery().forEntitiesAtRevision(Employee.class, 1);
    query.add(AuditEntity.relatedId("company").eq(1));
    for (Employee e : (List<Employee>) query.getResultList()) {
      System.out.println(e.getId() + ": " + e.getLastName() + " " + e.getFirstName());
    }
    // 1: Ralbern Michael
    // 2: Spencer Linda

    System.out.println("-------------");

    query = reader.createQuery().forEntitiesAtRevision(Employee.class, 2);
    query.add(AuditEntity.relatedId("company").eq(1));
    for (Employee e : (List<Employee>) query.getResultList()) {
      System.out.println(e.getId() + ": " + e.getLastName() + " " + e.getFirstName());
    }
    // 1: Ralbern Michael
    // 2: Spencer Linda

    System.out.println("-------------");

    query = reader.createQuery().forEntitiesAtRevision(Employee.class, 5);
    query.add(AuditEntity.relatedId("company").eq(1));
    for (Employee e : (List<Employee>) query.getResultList()) {
      System.out.println(e.getId() + ": " + e.getLastName() + " " + e.getFirstName());
    }
    // 3: Robinson Janet
    // 2: Spencer Linda

    System.out.println("-------------");
    query = reader.createQuery().forEntitiesAtRevision(Employee.class, 5);
    query.add(AuditEntity.property("lastName").eq("Spencer"));
    // query.add(AuditEntity.or(AuditEntity.property("lastName").eq("Spencer"),
    // AuditEntity.property("lastName").eq("Robinson")));
    for (Employee e : (List<Employee>) query.getResultList()) {
      System.out.println(e.getId() + ": " + e.getLastName() + " " + e.getFirstName());
    }
    // 2: Spencer Linda

    System.out.println("-------------");
    query = reader.createQuery().forEntitiesAtRevision(Employee.class,
        Employee.class.getName(), 5, true);
    for (Employee e : (List<Employee>) query.getResultList()) {
      System.out.println(e.getId() + ": " + e.getLastName() + " " + e.getFirstName());
    }
    // 3: Robinson Janet
    // 2: Spencer Linda
    // 1: null null

    System.out.println("===========================================");
    System.out.println("forEntitiesModifiedAtRevision");
    query = reader.createQuery().forEntitiesModifiedAtRevision(Employee.class, 1);
    for (Employee e : (List<Employee>) query.getResultList()) {
      System.out.println(e.getId() + ": " + e.getLastName() + " " + e.getFirstName());
    }
    // 1: Ralbern Michael
    // 2: Spencer Linda

    System.out.println("-------------");
    query = reader.createQuery().forEntitiesModifiedAtRevision(Employee.class, 2);
    for (Employee e : (List<Employee>) query.getResultList()) {
      System.out.println(e.getId() + ": " + e.getLastName() + " " + e.getFirstName());
    }
    // empty

    System.out.println("-------------");
    query = reader.createQuery().forEntitiesModifiedAtRevision(Employee.class, 5);
    for (Employee e : (List<Employee>) query.getResultList()) {
      System.out.println(e.getId() + ": " + e.getLastName() + " " + e.getFirstName());
    }
    // 1: null null

    System.out.println("===========================================");
    System.out.println("forRevisionsOfEntity");
    query = reader.createQuery().forRevisionsOfEntity(Employee.class, false, true);
    // query.add(AuditEntity.id().eq(1));
    List<Object[]> results = query.getResultList();
    for (Object[] result : results) {
      Employee employee = (Employee) result[0];
      CustomRevisionEntity revEntity = (CustomRevisionEntity) result[1];
      RevisionType revType = (RevisionType) result[2];

      System.out.println("Revision     : " + revEntity.getId());
      System.out.println("Revision Date: " + revEntity.getRevisionDate());
      System.out.println("User         : " + revEntity.getUsername());
      System.out.println("Type         : " + revType);
      System.out.println(
          "Employee     : " + employee.getLastName() + " " + employee.getFirstName());

      System.out.println("------------------------------------------------");
    }

    System.out.println("-------------");
    query = reader.createQuery().forRevisionsOfEntity(Employee.class, false, true);
    query.add(AuditEntity.revisionProperty("username").eq("Bob"));
    results = query.getResultList();
    for (Object[] result : results) {
      Employee employee = (Employee) result[0];
      CustomRevisionEntity revEntity = (CustomRevisionEntity) result[1];
      RevisionType revType = (RevisionType) result[2];

      System.out.println("Revision     : " + revEntity.getId());
      System.out.println("Revision Date: " + revEntity.getRevisionDate());
      System.out.println("User         : " + revEntity.getUsername());
      System.out.println("Type         : " + revType);
      System.out.println(
          "Employee     : " + employee.getLastName() + " " + employee.getFirstName());

      System.out.println("------------------------------------------------");
    }

    System.out.println("===========================================");
    System.out.println("forRevisionsOfEntityWithChanges");
    query = reader.createQuery().forRevisionsOfEntityWithChanges(Employee.class, true);
    results = query.getResultList();
    for (Object[] result : results) {
      Employee employee = (Employee) result[0];
      CustomRevisionEntity revEntity = (CustomRevisionEntity) result[1];
      RevisionType revType = (RevisionType) result[2];
      Set<String> properties = (Set<String>) result[3];

      System.out.println("Revision     : " + revEntity.getId());
      System.out.println("Revision Date: " + revEntity.getRevisionDate());
      System.out.println("User         : " + revEntity.getUsername());
      System.out.println("Type         : " + revType);
      System.out.println("Changed Props: " + properties);
      System.out.println(
          "Employee     : " + employee.getLastName() + " " + employee.getFirstName());

      System.out.println("------------------------------------------------");
    }

    em.close();
    JPAUtil.shutdown();
  }

}
