package com.reliaquest.api.integration.dto;

import com.reliaquest.api.dto.Employee;
import lombok.Data;

import java.util.List;

@Data
public class GetAllEmployeeResponseDto {
    private String status;
    private List<Employee> data;
}
