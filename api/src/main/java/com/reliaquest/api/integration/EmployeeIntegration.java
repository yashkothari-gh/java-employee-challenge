package com.reliaquest.api.integration;

import com.reliaquest.api.config.EmployeeUrlConfigs;
import com.reliaquest.api.dto.EmployeeInput;
import com.reliaquest.api.exception.*;
import com.reliaquest.api.integration.dto.EmployeeDeleteRequestDto;
import com.reliaquest.api.integration.dto.EmployeeDeleteResponseDto;
import com.reliaquest.api.integration.dto.EmployeeResponseDto;
import com.reliaquest.api.integration.dto.GetAllEmployeeResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class EmployeeIntegration {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeIntegration.class);
    private final RestTemplate restTemplate;
    private final EmployeeUrlConfigs employeeUrlConfigs;

    @Autowired
    public EmployeeIntegration(RestTemplate restTemplate, EmployeeUrlConfigs employeeUrlConfigs) {
        this.restTemplate = restTemplate;
        this.employeeUrlConfigs = employeeUrlConfigs;
    }

    public GetAllEmployeeResponseDto getAllEmployees() {
        String url = employeeUrlConfigs.getBaseUrl() + employeeUrlConfigs.getEmployeeResource();
        logger.info("Fetching all employees from URL: {}", url);
        try {
            ResponseEntity<GetAllEmployeeResponseDto> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    GetAllEmployeeResponseDto.class);

            logger.info("Received response status: {}", responseEntity.getStatusCode());
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                logger.info("Successfully fetched all employees.");
                return responseEntity.getBody();
            } else {
                logger.error("Failed to fetch employees: {}", responseEntity.getStatusCode());
                throw new EmployeeIntegrationException("Failed to fetch employees: " + responseEntity.getStatusCode(), null);
            }
        } catch (HttpClientErrorException.TooManyRequests e) {
            logger.warn("Rate limit exceeded while fetching all employees: {}", e.getMessage());
            throw new RateLimitExceededException("Too many requests. Please try again later.");
        } catch (Exception e) {
            logger.error("Error while fetching all employees", e);
            throw new EmployeeIntegrationException("Error while fetching all employees", e);
        }
    }

    public EmployeeResponseDto getEmployeeById(String id) {
        validateInput(id, "Employee ID");
        String url = employeeUrlConfigs.getBaseUrl() + employeeUrlConfigs.getEmployeeResource() + "/" + id;
        logger.info("Fetching employee with ID: {} from URL: {}", id, url);
        try {
            ResponseEntity<EmployeeResponseDto> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    EmployeeResponseDto.class);

            logger.info("Received response status: {}", responseEntity.getStatusCode());
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                EmployeeResponseDto responseBody = responseEntity.getBody();
                logger.info("Successfully fetched employee with ID: {}", id);
                return responseBody;
            } else {
                logger.error("Failed to fetch employee: {}", responseEntity.getStatusCode());
                throw new EmployeeIntegrationException("Failed to fetch employee: " + responseEntity.getStatusCode(), null);
            }
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Employee not found: {}", e.getResponseBodyAsString());
            throw new EmployeeNotFoundException("Employee not found. Please provide valid id.");
        } catch (HttpClientErrorException.TooManyRequests e) {
            logger.warn("Rate limit exceeded while fetching an employee: {}", e.getMessage());
            throw new RateLimitExceededException("Too many requests. Please try again later.");
        } catch (Exception e) {
            logger.error("Error while fetching employee with ID: {}", id, e);
            throw new EmployeeIntegrationException("Error while fetching employee with id: " + id, e);
        }
    }

    public EmployeeResponseDto createEmployee(String name, int salary, int age, String title) {
        validateEmployeeData(name, salary, age, title);
        String url = employeeUrlConfigs.getBaseUrl() + employeeUrlConfigs.getEmployeeResource();
        logger.info("Creating employee with name: {}, salary: {}, age: {} at URL: {}", name, salary, age, url);
        EmployeeInput newEmployee = new EmployeeInput(name, salary, age, title);
        HttpEntity<EmployeeInput> requestEntity = new HttpEntity<>(newEmployee, null);
        try {
            ResponseEntity<EmployeeResponseDto> responseEntity = restTemplate.postForEntity(url, requestEntity, EmployeeResponseDto.class);
            logger.info("Received responseEntity status for creation: {}", responseEntity.getStatusCode());
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                logger.info("Successfully created employee.");
                return responseEntity.getBody();
            } else {
                logger.error("Failed to create employee: {}", responseEntity.getStatusCode());
                throw new EmployeeCreationException("Failed to create employee: " + responseEntity.getStatusCode());
            }
        } catch (HttpClientErrorException.TooManyRequests e) {
            logger.warn("Rate limit exceeded while creating an employee: {}", e.getMessage());
            throw new RateLimitExceededException("Too many requests. Please try again later.");
        } catch (Exception e) {
            logger.error("Error while creating employee", e);
            throw new EmployeeIntegrationException("Error while creating employee", e);
        }
    }

    public void deleteEmployee(String name) {
        validateInput(name, "Employee name");
        String url = employeeUrlConfigs.getBaseUrl() + employeeUrlConfigs.getEmployeeResource();
        logger.info("Deleting employee with NAME: {} at URL: {}", name, url);

        EmployeeDeleteRequestDto employeeDeleteRequest = new EmployeeDeleteRequestDto(name);
        HttpEntity<EmployeeDeleteRequestDto> requestEntity = new HttpEntity<>(employeeDeleteRequest);

        try {
            ResponseEntity<EmployeeDeleteResponseDto> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    requestEntity,
                    EmployeeDeleteResponseDto.class
            );
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                if (responseEntity.getBody() != null && responseEntity.getBody().getData().equals(false)) {
                    logger.error("Failed to delete employee: {} with status ", responseEntity.getBody().getStatus());
                    throw new EmployeeDeletionException("Error while deleting employee with NAME: " + name);
                }
                logger.info("Successfully deleted employee with NAME: {}, Response: {}", name, responseEntity.getBody());
            } else {
                logger.error("Failed to delete employee with name: {} with status code: {}", name, responseEntity.getStatusCode());
                throw new EmployeeDeletionException("Error while deleting employee with NAME: " + name);
            }
        } catch (HttpClientErrorException.TooManyRequests e) {
            logger.warn("Rate limit exceeded while deleting an employee: {}", e.getMessage());
            throw new RateLimitExceededException("Too many requests. Please try again later.");
        } catch (Exception e) {
            logger.error("Error while deleting employee with NAME: {}", name, e);
            throw new EmployeeIntegrationException("Error while deleting employee with NAME: " + name, e);
        }
    }

    private void validateInput(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be null or empty");
        }
    }

    private void validateEmployeeData(String name, int salary, int age, String title) {
        if (name == null || name.trim().isEmpty()) {
            logger.warn("Invalid Employee name: {}", name);
            throw new IllegalArgumentException("Employee name must not be null or empty");
        }
        if (salary <= 0) {
            logger.warn("Invalid Employee salary: {}", salary);
            throw new IllegalArgumentException("Employee salary must be greater than zero");
        }
        if (age < 16 || age > 75) {
            logger.warn("Invalid Employee age: {}", age);
            throw new IllegalArgumentException("Employee age must be between 16 and 75");
        }
        if (title == null || title.trim().isEmpty()) {
            logger.warn("Invalid Employee name: {}", name);
            throw new IllegalArgumentException("Employee title must not be null or empty");
        }
    }
}
