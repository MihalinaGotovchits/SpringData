package org.example.service;

import org.example.model.Department;
import org.example.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Department getDepartmentById(Long departmentId) {
        return checkDepartment(departmentId);
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public Department create(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    public Department update(Long departmentId, Department department) {
        Department updateDepartment = checkDepartment(departmentId);
        updateDepartment.setName(department.getName());
        return departmentRepository.save(updateDepartment);
    }

    @Override
    public void deleteById(Long departmentId) {
        checkDepartment(departmentId);
        departmentRepository.deleteById(departmentId);
    }

    private Department checkDepartment(Long departmentId) {
        return departmentRepository.findById(departmentId).orElseThrow(() ->
                new RuntimeException("Отдел с Id " + departmentId + " не найден"));
    }
}
