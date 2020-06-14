package com.adisutanto.employeemanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class EmployeeRestController {

    private static final String USERS_UPLOAD = "/users/upload";
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeRestController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping(value = USERS_UPLOAD, consumes = "multipart/form-data")
    public String upload(@RequestParam MultipartFile file) {
        return employeeService.upload(file);
    }

}
