package com.adisutanto.employeemanagement;

import org.springframework.data.rest.core.config.Projection;

import java.math.BigDecimal;

@Projection(name = "employeeWithId", types = Employee.class)
public interface EmployeeProjection {
    String getId();

    String getLogin();

    String getName();

    BigDecimal getSalary();
}
