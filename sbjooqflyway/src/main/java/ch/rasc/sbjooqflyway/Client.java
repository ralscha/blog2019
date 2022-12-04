package ch.rasc.sbjooqflyway;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.rasc.sbjooqflyway.db.tables.pojos.Employee;

public class Client {

  public static void main(String[] args) throws IOException, InterruptedException {
    var objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();

    var client = HttpClient.newHttpClient();
    HttpResponse<String> response = client.send(
        HttpRequest.newBuilder().GET()
            .uri(URI.create("http://localhost:8080/listEmployees")).build(),
        BodyHandlers.ofString());

    List<Employee> employees = objectMapper.readValue(response.body(),
        new TypeReference<List<Employee>>() {
          // nothing here
        });
    System.out.println(employees);

    Employee newEmployee = new Employee();
    newEmployee.setGender("M");
    newEmployee.setFirstName("John");
    newEmployee.setLastName("Doe");
    newEmployee.setUserName("jdoe4");
    newEmployee.setBirthDate(LocalDate.of(1986, 12, 11));
    newEmployee.setHireDate(LocalDate.of(2014, 1, 1));
    newEmployee.setDepartmentId(1);

    String json = objectMapper.writeValueAsString(newEmployee);
    response = client.send(
        HttpRequest.newBuilder().POST(BodyPublishers.ofString(json))
            .header("Content-Type", "application/json")
            .uri(URI.create("http://localhost:8080/newEmployee")).build(),
        BodyHandlers.ofString());

    Employee insertedEmployee = objectMapper.readValue(response.body(), Employee.class);
    System.out.println(insertedEmployee);

  }

}
