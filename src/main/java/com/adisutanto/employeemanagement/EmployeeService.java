package com.adisutanto.employeemanagement;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Set;

@Slf4j
@Service
public class EmployeeService {
    private final EmployeeRepository repository;
    private final Validator validator;
    private final CsvMapper mapper;
    private final CsvSchema schema;

    @Autowired
    public EmployeeService(EmployeeRepository repository, Validator validator) {
        this.repository = repository;
        this.validator = validator;
        mapper = CsvMapper.builder().build();
        schema = mapper.schemaFor(Employee.class).withHeader().withComments().withStrictHeaders(true);
    }

    @Transactional
    public String upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ApplicationException("Uploaded file should not be empty");
        }
        try {
            MappingIterator<Employee> it = mapper.readerFor(Employee.class).with(schema)
                    .readValues(file.getInputStream());
            while (it.hasNextValue()) {
                Employee employee = it.nextValue();
                Set<ConstraintViolation<Employee>> violations = validator.validate(employee);
                if (!violations.isEmpty()) {
                    throw new ApplicationException(violations.toString());
                }
                repository.save(employee);
            }
            String msg = "Successfully uploaded " + file.getOriginalFilename();
            log.info(msg);
            return msg;
        } catch (JsonParseException | InvalidFormatException e) {
            throw new ApplicationException(e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
