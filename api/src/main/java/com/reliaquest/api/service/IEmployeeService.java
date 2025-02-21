package com.reliaquest.api.service;


import com.reliaquest.api.dto.Employee;

import java.util.List;

public interface IEmployeeService {

    List<Employee> getAllEmployees();

    List<Employee> getEmployeesByNameSearch(String name);

    Employee getEmployeeById(String id);

    Integer getHighestSalaryOfEmployees();

    List<String> getTopTenHighestEarningEmployeeNames();

    Employee createEmployee(String name, int salary, int age, String title);

    String deleteEmployeeById(String id);


}
