package com.adisutanto.employeemanagement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.math.BigDecimal;

@RepositoryRestResource(collectionResourceRel = "users", path = "users", excerptProjection = EmployeeProjection.class)
public interface EmployeeRepository extends PagingAndSortingRepository<Employee, String> {
    Page<Employee> findBySalaryBetween(@Param("minSalary") BigDecimal minSalary,
                                       @Param("maxSalary") BigDecimal maxSalary, Pageable p);
}
