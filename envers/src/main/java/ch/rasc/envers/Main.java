package ch.rasc.envers;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;

import org.hibernate.Session;

import net.efabrika.util.DBTablePrinter;

public class Main {

  public static void main(String[] args) {

    // Revision 1
    System.out.println("Revision 1: INSERT");

    EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
    CurrentUser.INSTANCE.logIn("Alice");

    em.getTransaction().begin();
    Company company = new Company();
    company.setName("E Corp");
    company.setCity("New York City");
    company.setStreet(null);

    Set<Employee> employees = new HashSet<>();

    Employee employee = new Employee();
    employee.setCompany(company);
    employee.setLastName("Spencer");
    employee.setFirstName("Linda");
    employee.setStreet("High Street 123");
    employee.setCity("Newark");
    employees.add(employee);

    employee = new Employee();
    employee.setCompany(company);
    employee.setLastName("Ralbern");
    employee.setFirstName("Michael");
    employee.setStreet("57th Street");
    employee.setCity("New York City");
    employees.add(employee);

    company.setEmployees(employees);

    em.persist(company);
    em.getTransaction().commit();

    printEnversTables();

    // Revision 2: Update Company
    System.out.println("Revision 2: UPDATE Company");
    CurrentUser.INSTANCE.logIn("Bob");

    em.getTransaction().begin();
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Company> q = cb.createQuery(Company.class);
    Root<Company> c = q.from(Company.class);
    ParameterExpression<String> p = cb.parameter(String.class);
    q.select(c).where(cb.equal(c.get("name"), p));

    TypedQuery<Company> query1 = em.createQuery(q);
    query1.setParameter(p, "E Corp");

    company = query1.getSingleResult();
    company.setName("EEE Corp");

    em.getTransaction().commit();

    printEnversTables();

    // Revision 3: New Employee
    System.out.println("Revision 3: New Employee");
    CurrentUser.INSTANCE.logIn("Bob");

    em.getTransaction().begin();
    employee = new Employee();
    employee.setCompany(company);
    employee.setLastName("Robinson");
    employee.setFirstName("Janet");
    employee.setCity("Greenwich");
    employee.setStreet("Walsh Ln 10");
    company.getEmployees().add(employee);
    em.getTransaction().commit();

    printEnversTables();

    // Revision 4: Update Employee
    System.out.println("Revision 4: Update Employee");
    CurrentUser.INSTANCE.logIn("Alice");

    em.getTransaction().begin();
    TypedQuery<Employee> query2 = createEmployeeQuery(em, "Linda", "Spencer");
    employee = query2.getSingleResult();
    employee.setStreet("101 W 91st St");
    employee.setCity("New York City");
    em.getTransaction().commit();

    printEnversTables();

    // Revision 5: Delete Employee
    System.out.println("Revision 5: Delete Employee");
    CurrentUser.INSTANCE.logIn("Alice");

    em.getTransaction().begin();
    TypedQuery<Employee> query3 = createEmployeeQuery(em, "Michael", "Ralbern");
    employee = query3.getSingleResult();
    employee.getCompany().getEmployees().remove(employee);
    em.remove(employee);
    em.getTransaction().commit();

    printEnversTables();

    em.close();
    JPAUtil.shutdown();
  }

  private static void printEnversTables() {
    try (Session session = JPAUtil.getEntityManagerFactory().createEntityManager()
        .unwrap(Session.class)) {
      session.doWork(connection -> {
        System.out.println("= ENVERS TABLES ========================");
        DBTablePrinter.printTable(connection, "REVINFO");
        DBTablePrinter.printTable(connection, "Company_AUD");
        DBTablePrinter.printTable(connection, "Employee_AUD");
        System.out.println("========================================");
      });
    }
  }

  private static TypedQuery<Employee> createEmployeeQuery(EntityManager em,
      String firstName, String lastName) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Employee> q = cb.createQuery(Employee.class);
    Root<Employee> c = q.from(Employee.class);
    ParameterExpression<String> p1 = cb.parameter(String.class);
    ParameterExpression<String> p2 = cb.parameter(String.class);
    q.select(c)
        .where(cb.and(cb.equal(c.get("firstName"), p1), cb.equal(c.get("lastName"), p2)));

    TypedQuery<Employee> query = em.createQuery(q);
    query.setParameter(p1, firstName);
    query.setParameter(p2, lastName);
    return query;
  }

}
