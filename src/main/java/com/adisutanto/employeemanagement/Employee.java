package com.adisutanto.employeemanagement;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "EMPLOYEES")
public class Employee {
    @Id
    @NotNull
    @Size(max = 10)
    private String id;
    @NotNull
    @Size(max = 20)
    private String login;
    @Size(max = 50)
    private String name;
    @PositiveOrZero
    private BigDecimal salary;
}
