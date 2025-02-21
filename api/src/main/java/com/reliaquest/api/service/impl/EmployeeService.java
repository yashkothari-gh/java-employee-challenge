package com.reliaquest.api.service.impl;

import com.reliaquest.api.dto.Employee;
import com.reliaquest.api.exception.EmployeeCreationException;
import com.reliaquest.api.exception.EmployeeIntegrationException;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.EmployeeServiceException;
import com.reliaquest.api.integration.EmployeeIntegration;
import com.reliaquest.api.integration.dto.EmployeeResponseDto;
import com.reliaquest.api.integration.dto.GetAllEmployeeResponseDto;
import com.reliaquest.api.service.IEmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService implements IEmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    private final EmployeeIntegration employeeIntegration;

    @Autowired
    public EmployeeService(EmployeeIntegration employeeIntegration) {
        this.employeeIntegration = employeeIntegration;
    }

    @Override
    public List<Employee> getAllEmployees() {
        logger.info("Received request to load all the employees.");
        try {
            GetAllEmployeeResponseDto getAllEmployeeResponseDto = employeeIntegration.getAllEmployees();
            logger.info("Done loading all the employees.");
            return getAllEmployeeResponseDto.getData();
        } catch (EmployeeIntegrationException e) {
            logger.error("Error occurred while fetching all the employees. Error : {}", e.getMessage());
            throw new EmployeeServiceException("Error fetching all employees", e);
        }
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(String name) {
        logger.info("Received request to load all the employees with search string : {}.", name);
        validateInput(name, "Search name");
        try {
            GetAllEmployeeResponseDto getAllEmployeeResponseDto = employeeIntegration.getAllEmployees();
            logger.info("Done loading all the employees with search string : {}.", name);
            return getAllEmployeeResponseDto.getData().stream()
                    .filter(employee -> employee.getEmployeeName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        } catch (EmployeeIntegrationException e) {
            logger.error("Error occurred while searching for employee. Error : {}", e.getMessage());
            throw new EmployeeServiceException("Error searching for employees by name", e);
        }
    }

    @Override
    public Employee getEmployeeById(String id) {
        logger.info("Received request to load employee by id: {}.", id);
        validateInput(id, "Employee ID");
        try {
            EmployeeResponseDto employeeResponseDto = employeeIntegration.getEmployeeById(id);
            logger.info("Done loading employee details.");
            return employeeResponseDto.getData();
        } catch (EmployeeNotFoundException e) {
            logger.error("Unable to find employee with id : {}", id);
            throw e;
        } catch (EmployeeIntegrationException e) {
            throw new EmployeeServiceException("Error fetching employee with id: " + id, e);
        }
    }

    @Override
    public Integer getHighestSalaryOfEmployees() {
        logger.info("Received request to fetch highest salary.");
        try {
            GetAllEmployeeResponseDto getAllEmployeeResponseDto = employeeIntegration.getAllEmployees();
            Optional<Integer> highestSalary = getAllEmployeeResponseDto.getData().stream()
                    .map(Employee::getEmployeeSalary)
                    .max(Integer::compareTo);
            return highestSalary.orElseThrow(() -> new EmployeeServiceException("No employees found to determine highest salary"));
        } catch (EmployeeIntegrationException e) {
            throw new EmployeeServiceException("Error fetching highest salary of employees", e);
        }
    }

    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        logger.info("Received request to fetch Top Ten Highest Earning Employees.");
        try {
            GetAllEmployeeResponseDto getAllEmployeeResponseDto = employeeIntegration.getAllEmployees();
            return getAllEmployeeResponseDto.getData().stream()
                    .sorted((e1, e2) -> Integer.compare(e2.getEmployeeSalary(), e1.getEmployeeSalary()))
                    .limit(10)
                    .map(Employee::getEmployeeName)
                    .collect(Collectors.toList());
        } catch (EmployeeIntegrationException e) {
            throw new EmployeeServiceException("Error fetching top 10 highest earning employee names", e);
        }
    }

    @Override
    public Employee createEmployee(String name, int salary, int age, String title) {
        logger.info("Received request to create new Employee.");
        validateEmployeeData(name, salary, age, title);
        try {
            EmployeeResponseDto employeeResponseDto = employeeIntegration.createEmployee(name, salary, age, title);
            logger.info("Successfully created new Employee.");
            return employeeResponseDto.getData();
        } catch (EmployeeCreationException e) {
            logger.error("Unable to create employee with name : {}", name);
            throw e;
        } catch (EmployeeIntegrationException e) {
            throw new EmployeeServiceException("Error creating employee", e);
        }
    }

    @Override
    public String deleteEmployeeById(String id) {
        validateInput(id, "Employee ID");
        logger.info("Received request to delete employee with id : {}", id);
        try {
            Employee employee = getEmployeeById(id);
            employeeIntegration.deleteEmployee(employee.getEmployeeName());
            logger.info("Successfully deleted employee with id : {}", id);
            return employee.getEmployeeName();
        } catch (EmployeeNotFoundException e) {
            logger.error("Unable to find employee with id : {}", id);
            throw e;
        } catch (EmployeeIntegrationException e) {
            throw new EmployeeServiceException("Error deleting employee with id: " + id, e);
        }
    }

    private void validateInput(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be null or empty");
        }
    }

    private void validateEmployeeData(String name, int salary, int age, String title) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name must not be null or empty");
        }
        if (salary <= 0) {
            throw new IllegalArgumentException("Employee salary must be greater than zero");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("Employee age must be greater than zero");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee title must not be null or empty");
        }
    }
}
