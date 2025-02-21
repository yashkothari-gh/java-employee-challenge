package com.reliaquest.api.controller;

import com.reliaquest.api.dto.Employee;
import com.reliaquest.api.dto.EmployeeInput;
import com.reliaquest.api.service.IEmployeeService;
import com.reliaquest.api.service.impl.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController implements IEmployeeController<Employee, EmployeeInput> {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    private final IEmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    @GetMapping()
    public ResponseEntity<List<Employee>> getAllEmployees() {
        logger.info("Fetching all employees.");
        List<Employee> employees = employeeService.getAllEmployees();
        logger.info("Successfully retrieved {} employees.", employees.size());
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @Override
    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) {
        logger.info("Searching employees with name containing: '{}'.", searchString);
        List<Employee> employees = employeeService.getEmployeesByNameSearch(searchString);
        logger.info("Found {} employees matching the search query: '{}'.", employees.size(), searchString);
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        logger.info("Fetching details for employee with ID: '{}'.", id);
        Employee employee = employeeService.getEmployeeById(id);
        logger.info("Successfully retrieved details for employee with ID: '{}'.", id);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @Override
    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        logger.info("Fetching the highest salary among all employees.");
        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
        logger.info("Highest salary retrieved: {}", highestSalary);
        return new ResponseEntity<>(highestSalary, HttpStatus.OK);
    }

    @Override
    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        logger.info("Fetching the names of the top 10 highest-earning employees.");
        List<String> topEarningEmployees = employeeService.getTopTenHighestEarningEmployeeNames();
        logger.info("Successfully retrieved top 10 highest-earning employees.");
        return new ResponseEntity<>(topEarningEmployees, HttpStatus.OK);
    }

    @Override
    @PostMapping()
    public ResponseEntity<Employee> createEmployee(@RequestBody EmployeeInput employeeInput) {
        logger.info("Creating a new employee with name: '{}', title: '{}'.", employeeInput.getName(), employeeInput.getTitle());
        Employee employee = employeeService.createEmployee(employeeInput.getName(), employeeInput.getSalary(), employeeInput.getAge(), employeeInput.getTitle());
        logger.info("Successfully created employee: '{}'.", employee.getEmployeeName());
        return new ResponseEntity<>(employee, HttpStatus.CREATED);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        logger.info("Deleting employee with ID: '{}'.", id);
        String employeeName = employeeService.deleteEmployeeById(id);
        logger.info("Successfully deleted employee with ID: '{}', Name: '{}'.", id, employeeName);
        return new ResponseEntity<>(employeeName, HttpStatus.OK);
    }
}
