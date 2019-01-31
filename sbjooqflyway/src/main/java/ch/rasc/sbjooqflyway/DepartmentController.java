package ch.rasc.sbjooqflyway;

import static ch.rasc.sbjooqflyway.db.tables.Department.DEPARTMENT;

import java.util.List;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import ch.rasc.sbjooqflyway.db.tables.daos.DepartmentDao;
import ch.rasc.sbjooqflyway.db.tables.pojos.Department;

@RestController
public class DepartmentController {
  private final DepartmentDao departmentDao;

  private final DSLContext dsl;

  public DepartmentController(DSLContext dsl, Configuration jooqConfiguration) {
    this.departmentDao = new DepartmentDao(jooqConfiguration);
    this.dsl = dsl;
  }

  @GetMapping("/departments")
  public List<Department> departments() {
    return this.departmentDao.findAll();
  }

  @GetMapping("/department/{no}")
  public Department findDepartment(@PathVariable("no") String no) {
    return this.dsl
            .selectFrom(DEPARTMENT)
            .where(DEPARTMENT.NO.eq(no))
            .fetchOne()
            .into(Department.class);
  }
}
