package com.reliaquest.api.controller;

import com.reliaquest.api.dto.Employee;
import com.reliaquest.api.dto.EmployeeInput;
import com.reliaquest.api.service.IEmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    private final String employeeId = UUID.randomUUID().toString();
    private MockMvc mockMvc;
    @Mock
    private IEmployeeService employeeService;
    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
    }

    @Test
    void testGetAllEmployees() throws Exception {
        List<Employee> employees = List.of(new Employee(employeeId, "John Doe", 50000, 30, "Engineer", "john.doe@example.com"));
        when(employeeService.getAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(employeeId))
                .andExpect(jsonPath("$[0].employee_name").value("John Doe"))
                .andExpect(jsonPath("$[0].employee_email").value("john.doe@example.com"));
        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    void testGetEmployeesByNameSearch() throws Exception {
        List<Employee> employees = List.of(new Employee(employeeId, "John Doe", 50000, 30, "Engineer", "john.doe@example.com"));
        when(employeeService.getEmployeesByNameSearch("John")).thenReturn(employees);

        mockMvc.perform(get("/employee/search/John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(employeeId))
                .andExpect(jsonPath("$[0].employee_name").value("John Doe"))
                .andExpect(jsonPath("$[0].employee_email").value("john.doe@example.com"));
        verify(employeeService, times(1)).getEmployeesByNameSearch("John");

    }

    @Test
    void testGetEmployeeById() throws Exception {
        Employee employee = new Employee(employeeId, "John Doe", 50000, 30, "Engineer", "john.doe@example.com");
        when(employeeService.getEmployeeById(employeeId)).thenReturn(employee);

        mockMvc.perform(get("/employee/" + employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employeeId))
                .andExpect(jsonPath("$.employee_name").value("John Doe"))
                .andExpect(jsonPath("$.employee_email").value("john.doe@example.com"));
        verify(employeeService, times(1)).getEmployeeById(employeeId);
    }

    @Test
    void testCreateEmployee() throws Exception {
        EmployeeInput input = new EmployeeInput("John Doe", 50000, 30, "Engineer");
        Employee employee = new Employee(employeeId, "John Doe", 50000, 30, "Engineer", "john.doe@example.com");
        when(employeeService.createEmployee(anyString(), anyInt(), anyInt(), anyString())).thenReturn(employee);

        mockMvc.perform(post("/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John Doe\",\"salary\":50000,\"age\":30,\"title\":\"Engineer\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(employeeId))
                .andExpect(jsonPath("$.employee_name").value("John Doe"))
                .andExpect(jsonPath("$.employee_email").value("john.doe@example.com"));
        verify(employeeService, times(1)).createEmployee(anyString(), anyInt(), anyInt(), anyString());
    }

    @Test
    void testDeleteEmployeeById() throws Exception {
        when(employeeService.deleteEmployeeById(employeeId)).thenReturn("John Doe");

        mockMvc.perform(delete("/employee/" + employeeId))
                .andExpect(status().isOk())
                .andExpect(content().string("John Doe"));
        verify(employeeService, times(1)).deleteEmployeeById(employeeId);
    }


    @Test
    void testGetHighestSalaryOfEmployees() throws Exception {
        Integer expectedSalary = 120000;

        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(expectedSalary);

        mockMvc.perform(get("/employee/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(expectedSalary));
        verify(employeeService, times(1)).getHighestSalaryOfEmployees();
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames() throws Exception {
        List<String> expectedNames = List.of("Alice", "Bob", "Charlie", "David", "Eve",
                "Frank", "Grace", "Hank", "Ivy", "Jack");

        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(expectedNames);

        mockMvc.perform(get("/employee/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0]").value("Alice"))
                .andExpect(jsonPath("$[1]").value("Bob"))
                .andExpect(jsonPath("$[9]").value("Jack"));

        verify(employeeService, times(1)).getTopTenHighestEarningEmployeeNames();
    }
}

