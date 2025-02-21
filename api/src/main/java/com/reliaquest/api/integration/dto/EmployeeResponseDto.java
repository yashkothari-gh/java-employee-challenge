package com.reliaquest.api.integration.dto;

import com.reliaquest.api.dto.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeResponseDto {

    private String status;
    private Employee data;

}
