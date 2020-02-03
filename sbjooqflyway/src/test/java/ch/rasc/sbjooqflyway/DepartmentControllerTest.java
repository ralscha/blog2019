package ch.rasc.sbjooqflyway;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

import ch.rasc.sbjooqflyway.db.tables.pojos.Department;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class DepartmentControllerTest {

  @Autowired
  private DSLContext dsl;

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  public void departments() {
    var typeRef = new ParameterizedTypeReference<List<Department>>() {
      // nothing_here
    };

    var response = this.restTemplate.exchange("/departments", HttpMethod.GET, null,
        typeRef);

    List<Department> departments = response.getBody();
    assertThat(departments.get(0).getId()).isEqualTo(1);
    assertThat(departments.get(0).getNo()).isEqualTo("d001");
    assertThat(departments.get(0).getName()).isEqualTo("Marketing");

    assertThat(departments.get(1).getId()).isEqualTo(2);
    assertThat(departments.get(1).getNo()).isEqualTo("d002");
    assertThat(departments.get(1).getName()).isEqualTo("Finance");

    assertThat(departments.get(2).getId()).isEqualTo(3);
    assertThat(departments.get(2).getNo()).isEqualTo("d003");
    assertThat(departments.get(2).getName()).isEqualTo("Human Resources");

    assertThat(departments.get(3).getId()).isEqualTo(4);
    assertThat(departments.get(3).getNo()).isEqualTo("d004");
    assertThat(departments.get(3).getName()).isEqualTo("Production");

    assertThat(departments.get(4).getId()).isEqualTo(5);
    assertThat(departments.get(4).getNo()).isEqualTo("d005");
    assertThat(departments.get(4).getName()).isEqualTo("Development");

    assertThat(departments.get(5).getId()).isEqualTo(6);
    assertThat(departments.get(5).getNo()).isEqualTo("d006");
    assertThat(departments.get(5).getName()).isEqualTo("Quality Management");

    assertThat(departments.get(6).getId()).isEqualTo(7);
    assertThat(departments.get(6).getNo()).isEqualTo("d007");
    assertThat(departments.get(6).getName()).isEqualTo("Sales");

    assertThat(departments.get(7).getId()).isEqualTo(8);
    assertThat(departments.get(7).getNo()).isEqualTo("d008");
    assertThat(departments.get(7).getName()).isEqualTo("Research");

    assertThat(departments.get(8).getId()).isEqualTo(9);
    assertThat(departments.get(8).getNo()).isEqualTo("d009");
    assertThat(departments.get(8).getName()).isEqualTo("Customer Service");

  }

}
