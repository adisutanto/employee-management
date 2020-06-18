package com.adisutanto.employeemanagement;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
public class SalaryForm {
    @NotNull
    @PositiveOrZero
    private BigDecimal minSalary = BigDecimal.ZERO;
    @NotNull
    @PositiveOrZero
    private BigDecimal maxSalary = new BigDecimal("9999999999");
}
