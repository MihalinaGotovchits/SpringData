package org.example.service;

import org.example.model.Employee;

import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    Employee getEmployeeById(Long employeeId);
    Employee create(Employee employee);
    Employee update(Long employeeId, Employee employee);
    void deleteById(Long employeeId);
}
