package com.employeemanagement.service;

import com.employeemanagement.dto.EmployeeRequest;
import com.employeemanagement.entity.Employee;
import com.employeemanagement.entity.User;
import com.employeemanagement.entity.Role;
import com.employeemanagement.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class EmployeeService {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private UserService userService; // Added
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
    
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }
    
    public Optional<Employee> getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }
    
    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department);
    }
    
    public List<Employee> searchEmployees(String searchTerm) {
        return employeeRepository.searchEmployees(searchTerm);
    }
    
    public Employee createEmployee(EmployeeRequest employeeRequest) {
        Employee employee = new Employee();
        employee.setFirstName(employeeRequest.getFirstName());
        employee.setLastName(employeeRequest.getLastName());
        employee.setEmail(employeeRequest.getEmail());
        employee.setPhone(employeeRequest.getPhone());
        employee.setDepartment(employeeRequest.getDepartment());
        employee.setSalary(employeeRequest.getSalary());
        employee.setDateOfJoining(employeeRequest.getDateOfJoining());
        
                Employee savedEmployee = employeeRepository.save(employee);
        if (StringUtils.hasText(employeeRequest.getPassword())) {
            User user = new User();
            user.setUsername(generateUsername(employeeRequest.getFirstName(), employeeRequest.getLastName()));
            user.setEmail(employeeRequest.getEmail());
            user.setPassword(passwordEncoder.encode(employeeRequest.getPassword()));
            user.setEmployeeId(savedEmployee.getId());
            user.setRoles(Set.of(Role.EMPLOYEE)); // Corrected from ROLE_EMPLOYEE
            user.setEnabled(true);
            
            userService.createUser(user);
        }
        return savedEmployee;
    }
    private String generateUsername(String firstName, String lastName) {
        // Generate username in format: firstname.lastname (lowercase)
        return (firstName + "." + lastName).toLowerCase().replaceAll("\\s+", "");
    }

    public Employee updateEmployee(Long id, EmployeeRequest employeeRequest) {
        Optional<Employee> existingEmployee = employeeRepository.findById(id);
        if (existingEmployee.isPresent()) {
            Employee employee = existingEmployee.get();
            employee.setFirstName(employeeRequest.getFirstName());
            employee.setLastName(employeeRequest.getLastName());
            employee.setEmail(employeeRequest.getEmail());
            employee.setPhone(employeeRequest.getPhone());
            employee.setDepartment(employeeRequest.getDepartment());
            employee.setSalary(employeeRequest.getSalary());
            employee.setDateOfJoining(employeeRequest.getDateOfJoining());
            
            return employeeRepository.save(employee);
        }
        return null;
    }
    
    public boolean deleteEmployee(Long id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public boolean existsByEmail(String email) {
        return employeeRepository.existsByEmail(email);
    }
    
    public boolean existsByEmailAndNotId(String email, Long id) {
        Optional<Employee> employee = employeeRepository.findByEmail(email);
        return employee.isPresent() && !employee.get().getId().equals(id);
    }
    
    public long countByDepartment(String department) {
        return employeeRepository.countByDepartment(department);
    }
}

