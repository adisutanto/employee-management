package com.adisutanto.employeemanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EmployeeController {
    private static final String USERS_UPLOAD_HTML = "/users/upload.html";
    private static final String USERS_UPLOAD = "/users/upload";
    private static final String MESSAGE = "message";
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping(USERS_UPLOAD_HTML)
    public String uploadHtml() {
        return USERS_UPLOAD;
    }

    @PostMapping(value = USERS_UPLOAD_HTML, consumes = "multipart/form-data")
    public String uploadHtml(@RequestParam MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            redirectAttributes.addFlashAttribute(MESSAGE, employeeService.upload(file));
        } catch (ApplicationException e) {
            redirectAttributes.addFlashAttribute(MESSAGE, e.getMessage());
        }
        return "redirect:" + USERS_UPLOAD_HTML;
    }

}
