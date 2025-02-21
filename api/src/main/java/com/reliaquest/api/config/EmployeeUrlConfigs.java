package com.reliaquest.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "employee")
@Getter
@Setter
public class EmployeeUrlConfigs {
    private String baseUrl;
    private String employeeResource;
}
