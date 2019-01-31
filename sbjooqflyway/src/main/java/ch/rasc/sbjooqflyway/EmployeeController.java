package ch.rasc.sbjooqflyway;

import static ch.rasc.sbjooqflyway.db.tables.Employee.EMPLOYEE;

import java.time.LocalDate;
import java.util.List;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.rasc.sbjooqflyway.db.tables.daos.EmployeeDao;
import ch.rasc.sbjooqflyway.db.tables.pojos.Employee;

@RestController
public class EmployeeController {

  private final EmployeeDao employeeDao;

  private final DSLContext dsl;

  private final TransactionTemplate transactionTemplate;

  public EmployeeController(DSLContext dsl, Configuration jooqConfiguration,
      TransactionTemplate transactionTemplate) {
    this.employeeDao = new EmployeeDao(jooqConfiguration);
    this.dsl = dsl;
    this.transactionTemplate = transactionTemplate;
  }

  @GetMapping("/listEmployees")
  public List<Employee> employees() {
    return this.employeeDao.findAll();
  }

  @PostMapping("/deleteEmployee")
  public void delete(@RequestParam("id") int id) {
    this.employeeDao.deleteById(id);
  }

  @PostMapping("/newEmployee")
  public Employee newEmployee(@RequestBody Employee newEmployee) {
    this.employeeDao.insert(newEmployee);
    System.out.println(newEmployee.getId());
    return newEmployee;
  }

  @PostMapping("/updateEmployee")
  public Employee updateEmployee(@RequestBody Employee newEmployee) {
    this.employeeDao.update(newEmployee);
    return newEmployee;
  }

  @GetMapping("/findEmployees/{name}")
  public Integer[] findEmployees(@PathVariable("name") String name) {
    return this.dsl
            .select(EMPLOYEE.ID)
            .from(EMPLOYEE)
            .where(EMPLOYEE.FIRST_NAME.contains(name).or(EMPLOYEE.FIRST_NAME.contains(name)))
            .fetchArray(EMPLOYEE.ID);
  }

  @GetMapping("/countEmployees")
  public int count() {
    return this.dsl.fetchCount(EMPLOYEE);
  }

  @GetMapping("/insertMultiple1")
  @Transactional
  public void insertMultiple1() {
    Employee e1 = new Employee();
    e1.setFirstName("1");
    e1.setLastName("1");
    e1.setUserName("1");
    e1.setBirthDate(LocalDate.now());
    e1.setGender("M");
    e1.setHireDate(LocalDate.now());
    this.employeeDao.insert(e1);

    if (true) {
      throw new NullPointerException();
    }

    Employee e2 = new Employee();
    e2.setFirstName("2");
    e2.setLastName("2");
    e2.setUserName("2");
    e2.setBirthDate(LocalDate.now());
    e2.setGender("M");
    e2.setHireDate(LocalDate.now());
    this.employeeDao.insert(e2);
  }

  @GetMapping("/insertMultiple2")
  public void insertMultiple2() {
    this.transactionTemplate.execute(txStatus -> {
      Employee e1 = new Employee();
      e1.setFirstName("1");
      e1.setLastName("1");
      e1.setUserName("1");
      e1.setBirthDate(LocalDate.now());
      e1.setGender("M");
      e1.setHireDate(LocalDate.now());
      this.employeeDao.insert(e1);

      if (true) {
        throw new NullPointerException();
      }

      Employee e2 = new Employee();
      e2.setFirstName("2");
      e2.setLastName("2");
      e2.setUserName("2");
      e2.setBirthDate(LocalDate.now());
      e2.setGender("M");
      e2.setHireDate(LocalDate.now());
      this.employeeDao.insert(e2);

      return null;
    });
  }

  @GetMapping("/insertMultiple3")
  public void insertMultiple3() {

    this.dsl.transaction(txConf -> {
      EmployeeDao txEd = new EmployeeDao(txConf);

      Employee e1 = new Employee();
      e1.setFirstName("1");
      e1.setLastName("1");
      e1.setUserName("1");
      e1.setBirthDate(LocalDate.now());
      e1.setGender("M");
      e1.setHireDate(LocalDate.now());
      txEd.insert(e1);

      int count = DSL.using(txConf).fetchCount(EMPLOYEE);
      System.out.println(count);

      if (true) {
        throw new NullPointerException();
      }

      Employee e2 = new Employee();
      e2.setFirstName("2");
      e2.setLastName("2");
      e2.setUserName("2");
      e2.setBirthDate(LocalDate.now());
      e2.setGender("M");
      e2.setHireDate(LocalDate.now());
      txEd.insert(e2);
    });
  }
}
