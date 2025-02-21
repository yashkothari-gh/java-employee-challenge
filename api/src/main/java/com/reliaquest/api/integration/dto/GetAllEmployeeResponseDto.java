package com.reliaquest.api.integration.dto;

import com.reliaquest.api.dto.Employee;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GetAllEmployeeResponseDto {
    private String status;
    private List<Employee> data;

    public GetAllEmployeeResponseDto(List<Employee> employeeList) {
        this.data = employeeList;
        this.status = "";
    }
}
