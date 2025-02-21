package com.reliaquest.api;

import com.reliaquest.api.controller.EmployeeController;
import com.reliaquest.api.integration.EmployeeIntegration;
import com.reliaquest.api.service.impl.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ApiApplicationTest {

    @Autowired
    private EmployeeController employeeController;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeIntegration employeeIntegration;

    @Test
    void contextLoads() {
        assertNotNull(employeeController, "EmployeeController should be loaded in context");
        assertNotNull(employeeService, "EmployeeService should be loaded in context");
        assertNotNull(employeeIntegration, "EmployeeIntegration should be loaded in context");
    }
}
