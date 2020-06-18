package com.adisutanto.employeemanagement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeUploadIntegTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EmployeeService employeeService;

    private ResponseEntity<String> upload(String csv) {
        Resource resource = new ClassPathResource(csv);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);
        return this.restTemplate.postForEntity("/users/upload", body, String.class);
    }

    @Test
    public void whenInvalidContent() throws Exception {
        ResponseEntity<String> response = upload("insert_employees.sql");
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Expected header");
    }

    @Test
    public void whenUploadEmpty() throws Exception {
        ResponseEntity<String> response = upload("empty.csv");
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("empty");
    }

    @Test
    public void whenTooManyColumns() throws Exception {
        ResponseEntity<String> response = upload("too_many_columns.csv");
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Extra header");
    }

    @Test
    public void whenTooFewColumns() throws Exception {
        ResponseEntity<String> response = upload("too_few_columns.csv");
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Missing header");
    }

    @Test
    public void whenIncorrectFormatSalary() throws Exception {
        ResponseEntity<String> response = upload("incorrect_format_salary.csv");
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Cannot deserialize value");
    }

    @Test
    public void whenNegativeSalary() throws Exception {
        ResponseEntity<String> response = upload("negative_salary.csv");
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("must be greater than or equal to 0");
    }

    @Sql("/insert_employees.sql")
    @DirtiesContext
    @Test
    public void whenLoginConflict() throws Exception {
        ResponseEntity<String> response = upload("login_conflict.csv");
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("ConstraintViolationException");
        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "employees")).isEqualTo(1);
    }

    @Sql("/insert_employees.sql")
    @DirtiesContext
    @Test
    public void whenNormal() throws Exception {
        ResponseEntity<String> response = upload("employees.csv");
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "employees")).isEqualTo(3);
        String sql = "SELECT * FROM employees WHERE id = ?";
        // Verify update is correct
        Employee employee = jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(Employee.class), "e0001");
        assertThat(employee.getLogin()).isEqualTo("rwesley");
        assertThat(employee.getName()).isEqualTo("Ron Weasley");
        assertThat(employee.getSalary()).isEqualTo(new BigDecimal("19234.50"));
        // Verify insert is correct
        employee = jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(Employee.class), "e0011");
        assertThat(employee.getLogin()).isEqualTo("cushanpan");
        assertThat(employee.getName()).isEqualTo("麤羴攀");
        assertThat(employee.getSalary()).isEqualTo(new BigDecimal("0.01"));
    }

    @DirtiesContext
    @Test
    public void whenConcurrent() throws Exception {
        Callable<ResponseEntity<String>> task = () -> {
            return upload("employees.csv");
        };
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<ResponseEntity<String>> future1 = executor.submit(task);
        Future<ResponseEntity<String>> future2 = executor.submit(task);
        ResponseEntity<String> response1 = future1.get();
        ResponseEntity<String> response2 = future2.get();
        assertThat(response1.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(response2.getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(response2.getBody()).contains("ConstraintViolationException");
        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "employees")).isEqualTo(3);
    }

}
