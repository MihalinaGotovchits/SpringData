package org.example.service;

import org.example.model.Department;

import java.util.List;

public interface DepartmentService {
    Department getDepartmentById(Long departmentId);
    List<Department> getAllDepartments();
    Department create(Department department);
    Department update(Long departmentId, Department department);
    void deleteById(Long departmentId);
}
