package org.example.service;

import org.example.model.Employee;
import org.example.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployeeById(Long employeeId) {
        return checkEmployee(employeeId);
    }

    @Override
    public Employee create(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public Employee update(Long employeeId, Employee employee) {
        Employee employeeUpdate = checkEmployee(employeeId);
        employeeUpdate.setFirstName(employee.getFirstName());
        employeeUpdate.setLastName(employee.getLastName());
        employeeUpdate.setDepartment(employee.getDepartment());
        employeeUpdate.setPosition(employeeUpdate.getPosition());
        employeeUpdate.setSalary(employeeUpdate.getSalary());
        return employeeRepository.save(employeeUpdate);
    }

    @Override
    public void deleteById(Long employeeId) {
        checkEmployee(employeeId);
        employeeRepository.deleteById(employeeId);
    }

    private Employee checkEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId).orElseThrow(() ->
                new RuntimeException("Сотрудник с Id " + employeeId + " не найден"));
    }
}
