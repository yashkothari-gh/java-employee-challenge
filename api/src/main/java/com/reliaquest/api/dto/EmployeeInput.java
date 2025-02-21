package com.reliaquest.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeInput {

    @JsonProperty("name")
    private String name;

    @JsonProperty("salary")
    private Integer salary;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("title")
    private String title;
}
