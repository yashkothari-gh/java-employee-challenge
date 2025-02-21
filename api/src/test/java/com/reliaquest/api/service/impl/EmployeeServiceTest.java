package com.reliaquest.api.service.impl;

import com.reliaquest.api.dto.Employee;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.EmployeeServiceException;
import com.reliaquest.api.integration.EmployeeIntegration;
import com.reliaquest.api.integration.dto.EmployeeResponseDto;
import com.reliaquest.api.integration.dto.GetAllEmployeeResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeIntegration employeeIntegration;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee1;
    private Employee employee2;
    private List<Employee> employeeList;

    @BeforeEach
    void setUp() {
        employee1 = new Employee(UUID.randomUUID().toString(), "John Doe", 100000, 30, "Software Engineer", "john.doe@example.com");
        employee2 = new Employee(UUID.randomUUID().toString(), "Jane Smith", 120000, 28, "Senior Engineer", "jane.smith@example.com");

        employeeList = Arrays.asList(employee1, employee2);
    }

    @Test
    void testGetAllEmployees_Success() {
        GetAllEmployeeResponseDto responseDto = new GetAllEmployeeResponseDto(employeeList);
        when(employeeIntegration.getAllEmployees()).thenReturn(responseDto);

        List<Employee> result = employeeService.getAllEmployees();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(employeeIntegration, times(1)).getAllEmployees();
    }

    @Test
    void testGetAllEmployees_IntegrationFailure() {
        when(employeeIntegration.getAllEmployees()).thenThrow(new EmployeeServiceException("Integration error"));

        assertThrows(EmployeeServiceException.class, () -> employeeService.getAllEmployees());
        verify(employeeIntegration, times(1)).getAllEmployees();
    }

    @Test
    void testGetEmployeesByNameSearch_Success() {
        String searchName = "John";
        String employeeId1 = UUID.randomUUID().toString();
        String employeeId2 = UUID.randomUUID().toString();

        List<Employee> employees = List.of(
                new Employee(employeeId1, "John Doe", 75000, 28, "Software Engineer", "john.doe@example.com"),
                new Employee(employeeId2, "Johnny Smith", 90000, 35, "Tech Lead", "johnny.smith@example.com")
        );

        GetAllEmployeeResponseDto responseDto = new GetAllEmployeeResponseDto(employees);


        when(employeeIntegration.getAllEmployees()).thenReturn(responseDto);


        List<Employee> result = employeeService.getEmployeesByNameSearch(searchName);


        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getEmployeeName().toLowerCase().contains(searchName.toLowerCase()));
        assertTrue(result.get(1).getEmployeeName().toLowerCase().contains(searchName.toLowerCase()));


        verify(employeeIntegration, times(1)).getAllEmployees();
    }

    @Test
    void testGetEmployeesByNameSearch_NoMatch() {

        String searchName = "Nonexistent";
        List<Employee> employees = List.of(
                new Employee(UUID.randomUUID().toString(), "Alice Brown", 110000, 32, "Tech Lead", "alice.brown@example.com")
        );

        GetAllEmployeeResponseDto responseDto = new GetAllEmployeeResponseDto(employees);


        when(employeeIntegration.getAllEmployees()).thenReturn(responseDto);


        List<Employee> result = employeeService.getEmployeesByNameSearch(searchName);


        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(employeeIntegration, times(1)).getAllEmployees();
    }

    @Test
    void testGetEmployeeById_Success() {

        String employeeId = UUID.randomUUID().toString();
        Employee expectedEmployee = new Employee(employeeId, "Alice Brown", 120000, 34, "Engineering Manager", "alice.brown@example.com");
        EmployeeResponseDto responseDto = new EmployeeResponseDto("success", expectedEmployee);


        when(employeeIntegration.getEmployeeById(employeeId)).thenReturn(responseDto);


        Employee result = employeeService.getEmployeeById(employeeId);


        assertNotNull(result);
        assertEquals(employeeId, result.getId());
        assertEquals("Alice Brown", result.getEmployeeName());
        assertEquals("alice.brown@example.com", result.getEmployeeEmail());


        verify(employeeIntegration, times(1)).getEmployeeById(employeeId);
    }

    @Test
    void testGetEmployeeById_EmployeeNotFound() {

        String employeeId = UUID.randomUUID().toString();


        when(employeeIntegration.getEmployeeById(employeeId)).thenThrow(new EmployeeNotFoundException("Employee not found"));

        EmployeeNotFoundException thrown = assertThrows(EmployeeNotFoundException.class,
                () -> employeeService.getEmployeeById(employeeId));

        assertEquals("Employee not found", thrown.getMessage());

        verify(employeeIntegration, times(1)).getEmployeeById(employeeId);
    }


    @Test
    void testGetHighestSalaryOfEmployees_Success() {
        GetAllEmployeeResponseDto responseDto = new GetAllEmployeeResponseDto(employeeList);
        when(employeeIntegration.getAllEmployees()).thenReturn(responseDto);

        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();

        assertEquals(120000, highestSalary);
        verify(employeeIntegration, times(1)).getAllEmployees();
    }

    @Test
    void testGetHighestSalaryOfEmployees_NoEmployees() {
        GetAllEmployeeResponseDto responseDto = new GetAllEmployeeResponseDto(List.of());
        when(employeeIntegration.getAllEmployees()).thenReturn(responseDto);

        assertThrows(EmployeeServiceException.class, () -> employeeService.getHighestSalaryOfEmployees());
        verify(employeeIntegration, times(1)).getAllEmployees();
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames_Success() {
        GetAllEmployeeResponseDto responseDto = new GetAllEmployeeResponseDto(employeeList);
        when(employeeIntegration.getAllEmployees()).thenReturn(responseDto);

        List<String> topEarningEmployees = employeeService.getTopTenHighestEarningEmployeeNames();

        assertNotNull(topEarningEmployees);
        assertEquals(2, topEarningEmployees.size());
        assertEquals("Jane Smith", topEarningEmployees.get(0));
        verify(employeeIntegration, times(1)).getAllEmployees();
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames_EmptyList() {

        List<Employee> employees = Collections.emptyList();
        GetAllEmployeeResponseDto responseDto = new GetAllEmployeeResponseDto(employees);

        when(employeeIntegration.getAllEmployees()).thenReturn(responseDto);


        List<String> result = employeeService.getTopTenHighestEarningEmployeeNames();


        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(employeeIntegration, times(1)).getAllEmployees();
    }

    @Test
    void testCreateEmployee_Success() {
        String employeeId = UUID.randomUUID().toString();


        Employee newEmployee = new Employee(employeeId, "Alice Brown", 110000, 32, "Tech Lead", "alice.brown@example.com");
        EmployeeResponseDto responseDto = new EmployeeResponseDto("success", newEmployee);


        when(employeeIntegration.createEmployee("Alice Brown", 110000, 32, "Tech Lead"))
                .thenReturn(responseDto);


        Employee createdEmployee = employeeService.createEmployee("Alice Brown", 110000, 32, "Tech Lead");


        assertNotNull(createdEmployee);
        assertEquals("Alice Brown", createdEmployee.getEmployeeName());
        assertEquals("alice.brown@example.com", createdEmployee.getEmployeeEmail());
        assertNotNull(createdEmployee.getId());


        verify(employeeIntegration, times(1))
                .createEmployee("Alice Brown", 110000, 32, "Tech Lead");
    }

    @Test
    void testCreateEmployee_InvalidName() {
        assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee("", 50000, 25, "Developer"));
    }

    @Test
    void testDeleteEmployeeById_Success() {
        String employeeId = employee1.getId();

        when(employeeIntegration.getEmployeeById(employeeId)).thenReturn(new EmployeeResponseDto("", employee1));
        doNothing().when(employeeIntegration).deleteEmployee(employee1.getEmployeeName());

        String deletedEmployeeName = employeeService.deleteEmployeeById(employeeId);

        assertEquals(employee1.getEmployeeName(), deletedEmployeeName);
        verify(employeeIntegration, times(1)).deleteEmployee(employee1.getEmployeeName());
    }

    @Test
    void testDeleteEmployeeById_NotFound() {
        String nonExistentId = UUID.randomUUID().toString();

        when(employeeIntegration.getEmployeeById(nonExistentId)).thenThrow(new EmployeeServiceException("Employee not found"));

        assertThrows(EmployeeServiceException.class, () -> employeeService.deleteEmployeeById(nonExistentId));
        verify(employeeIntegration, times(1)).getEmployeeById(nonExistentId);
    }
}
