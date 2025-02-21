package com.reliaquest.api.integration;

import com.reliaquest.api.config.EmployeeUrlConfigs;
import com.reliaquest.api.dto.Employee;
import com.reliaquest.api.dto.EmployeeInput;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.RateLimitExceededException;
import com.reliaquest.api.integration.dto.EmployeeDeleteResponseDto;
import com.reliaquest.api.integration.dto.EmployeeResponseDto;
import com.reliaquest.api.integration.dto.GetAllEmployeeResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class EmployeeIntegrationTest {

    private final String baseUrl = "http://mock-api.com/employees";
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private EmployeeUrlConfigs employeeUrlConfigs;
    @InjectMocks
    private EmployeeIntegration employeeIntegration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(employeeUrlConfigs.getBaseUrl()).thenReturn(baseUrl);
        when(employeeUrlConfigs.getEmployeeResource()).thenReturn("/employee");
    }

    @Test
    void testGetAllEmployees_Success() {
        String url = baseUrl + "/employee";
        GetAllEmployeeResponseDto mockResponse = new GetAllEmployeeResponseDto(
                List.of(new Employee(UUID.randomUUID().toString(), "Alice", 50000, 30, "Engineer", "alice@example.com")));

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(GetAllEmployeeResponseDto.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        GetAllEmployeeResponseDto response = employeeIntegration.getAllEmployees();

        assertNotNull(response);
        assertEquals(1, response.getData().size());
        assertEquals("Alice", response.getData().get(0).getEmployeeName());
    }

    @Test
    void testGetAllEmployees_TooManyRequests() {
        String url = baseUrl + "/employee";

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(GetAllEmployeeResponseDto.class)))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Too Many Requests",
                        HttpHeaders.EMPTY,
                        null,
                        null
                ));

        RateLimitExceededException exception = assertThrows(RateLimitExceededException.class, () -> employeeIntegration.getAllEmployees());

        assertEquals("Too many requests. Please try again later.", exception.getMessage());
    }


    @Test
    void testGetEmployeeById_Success() {
        String id = UUID.randomUUID().toString();
        String url = baseUrl + "/employee/" + id;

        EmployeeResponseDto mockResponse = new EmployeeResponseDto("success",
                new Employee(id, "Bob", 60000, 35, "Manager", "bob@example.com"));

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(EmployeeResponseDto.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        EmployeeResponseDto response = employeeIntegration.getEmployeeById(id);

        assertNotNull(response);
        assertEquals("Bob", response.getData().getEmployeeName());
    }


    @Test
    void testGetEmployeeById_NotFound() {
        String employeeId = UUID.randomUUID().toString();
        String url = baseUrl + "/employee/" + employeeId;

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(EmployeeResponseDto.class)))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.NOT_FOUND,
                        "Not Found",
                        HttpHeaders.EMPTY,
                        null,
                        null
                ));

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class,
                () -> employeeIntegration.getEmployeeById(employeeId));

        assertEquals("Employee not found. Please provide valid id.", exception.getMessage());
    }


    @Test
    void testCreateEmployee_Success() {
        String url = baseUrl + "/employee";
        EmployeeInput input = new EmployeeInput("Charlie", 70000, 28, "Tech Lead");
        EmployeeResponseDto mockResponse = new EmployeeResponseDto("success",
                new Employee(UUID.randomUUID().toString(), "Charlie", 70000, 28, "Tech Lead", "charlie@example.com"));

        when(restTemplate.postForEntity(eq(url), any(), eq(EmployeeResponseDto.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        EmployeeResponseDto response = employeeIntegration.createEmployee("Charlie", 70000, 28, "Tech Lead");

        assertNotNull(response);
        assertEquals("Charlie", response.getData().getEmployeeName());
    }

    @Test
    void testCreateEmployee_TooManyRequests() {

        String url = baseUrl + "/employee";
        EmployeeInput input = new EmployeeInput("Charlie", 70000, 28, "Tech Lead");
        when(restTemplate.postForEntity(eq(url), any(), eq(EmployeeResponseDto.class)))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Too Many Requests",
                        HttpHeaders.EMPTY,
                        null,
                        null
                ));

        RateLimitExceededException exception = assertThrows(RateLimitExceededException.class, () -> employeeIntegration.createEmployee("Charlie", 70000, 28, "Tech Lead"));
        assertEquals("Too many requests. Please try again later.", exception.getMessage());
    }


    @Test
    void testDeleteEmployee_Success() {
        String url = baseUrl + "/employee";
        EmployeeDeleteResponseDto mockResponse = new EmployeeDeleteResponseDto("success", true);

        when(restTemplate.exchange(eq(url), eq(HttpMethod.DELETE), any(), eq(EmployeeDeleteResponseDto.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        assertDoesNotThrow(() -> employeeIntegration.deleteEmployee("David"));
    }

    @Test
    void testDeleteEmployee_TooManyRequests() {

        String url = baseUrl + "/employee";
        when(restTemplate.exchange(eq(url), eq(HttpMethod.DELETE), any(), eq(EmployeeDeleteResponseDto.class)))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Too Many Requests",
                        HttpHeaders.EMPTY,
                        null,
                        null
                ));

        RateLimitExceededException exception = assertThrows(RateLimitExceededException.class, () -> employeeIntegration.deleteEmployee("David"));
        assertEquals("Too many requests. Please try again later.", exception.getMessage());
    }


}
