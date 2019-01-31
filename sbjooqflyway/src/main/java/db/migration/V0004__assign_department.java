package db.migration;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.using;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.springframework.core.io.ClassPathResource;

public class V0004__assign_department extends BaseJavaMigration {

  @Override
  public void migrate(Context context) throws Exception {
    Map<String, String> userToDepartment = new HashMap<>();

    ClassPathResource cpr = new ClassPathResource("employee_department.xlsx");
    try (InputStream is = cpr.getInputStream();
        Workbook wb = WorkbookFactory.create(is)) {

      Sheet sheet = wb.getSheetAt(0);
      if (sheet != null) {
        Iterator<Row> rowIt = sheet.rowIterator();
        while (rowIt.hasNext()) {
          Row row = rowIt.next();
          userToDepartment.put(row.getCell(0).getStringCellValue(),
              row.getCell(1).getStringCellValue());
        }
      }
    }

    try (DSLContext dsl = using(context.getConnection())) {
      for (Map.Entry<String, String> entry : userToDepartment.entrySet()) {
        dsl.update(table("employee"))
            .set(field("department_id", Integer.class), 
                 select(field("id", Integer.class))
                 .from(table("department"))
                 .where(field("no", String.class).eq(entry.getValue())).limit(1).asField())
            .where(field("user_name", String.class).eq(entry.getKey())).execute();
      }

      int employeesWithUnsetDep = dsl.selectCount()
          .from(table("employee"))
          .innerJoin(table("department"))
          .on(field("department.id", Integer.class)
              .equal(field("employee.department_id", Integer.class)))
          .where(field("department.no", String.class).eq("dddd"))
          .fetchOne(0, Integer.class);

      if (employeesWithUnsetDep == 0) {
        dsl.delete(table("department")).where(field("no").eq("dddd")).execute();
      }

    }

  }
}