package com.adisutanto.employeemanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class EmployeeController {
    private static final String INDEX = "index";
    private static final String USERS_UPLOAD_HTML = "/users/upload.html";
    private static final String USERS_UPLOAD = "/users/upload";
    private static final String MESSAGE = "message";
    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeController(EmployeeService employeeService, EmployeeRepository employeeRepository) {
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/")
    public String index(Model model, @PageableDefault(size = 30, sort = "id") Pageable pageable,
                        @Valid SalaryForm salaryForm, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            Page<Employee> page = employeeRepository.findBySalaryBetween(salaryForm.getMinSalary(),
                    salaryForm.getMaxSalary(), pageable);
            model.addAttribute("page", page);
        } else {
            model.addAttribute("page", Page.empty(pageable));
        }
        model.addAttribute("salaryForm", salaryForm);
        model.addAttribute("nextSort", getNextSort(pageable.getSort()));
        return INDEX;
    }

    private Map<String, String> getNextSort(Sort sort) {
        Map<String, String> nextSort = new LinkedHashMap<>();
        nextSort.put("id", nextSort(sort.getOrderFor("id")));
        nextSort.put("login", nextSort(sort.getOrderFor("login")));
        nextSort.put("name", nextSort(sort.getOrderFor("name")));
        nextSort.put("salary", nextSort(sort.getOrderFor("salary")));
        return nextSort;
    }

    private String nextSort(Sort.Order order) {
        return (order == null || order.isDescending()) ? "asc" : "desc";
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
