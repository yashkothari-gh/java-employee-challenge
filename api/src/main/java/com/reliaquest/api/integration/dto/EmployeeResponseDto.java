package com.reliaquest.api.integration.dto;

import com.reliaquest.api.dto.Employee;
import lombok.Data;

@Data
public class EmployeeResponseDto {

    private String status;
    private Employee data;

}
