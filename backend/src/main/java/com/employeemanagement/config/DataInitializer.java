package com.employeemanagement.config;

import com.employeemanagement.entity.Employee;
import com.employeemanagement.entity.Role;
import com.employeemanagement.entity.User;
import com.employeemanagement.repository.EmployeeRepository;
import com.employeemanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Initialize sample employees if database is empty
        if (employeeRepository.count() == 0) {
            initializeEmployees();
        }
        
        // Initialize sample users if database is empty
        if (userRepository.count() == 0) {
            initializeUsers();
        }
    }
    
    private void initializeEmployees() {
        // Create sample employees
        Employee emp1 = new Employee("John", "Doe", "john.doe@company.com", "+1-555-0101",
                "Engineering", new BigDecimal("75000"), LocalDate.of(2022, 1, 15));
        
        Employee emp2 = new Employee("Jane", "Smith", "jane.smith@company.com", "+1-555-0102",
                "Marketing", new BigDecimal("65000"), LocalDate.of(2022, 3, 10));
        
        Employee emp3 = new Employee("Mike", "Johnson", "mike.johnson@company.com", "+1-555-0103",
                "Engineering", new BigDecimal("80000"), LocalDate.of(2021, 8, 20));
        
        Employee emp4 = new Employee("Sarah", "Wilson", "sarah.wilson@company.com", "+1-555-0104",
                "HR", new BigDecimal("70000"), LocalDate.of(2022, 5, 5));
        
        Employee emp5 = new Employee("Admin", "User", "admin@company.com", "+1-555-0100",
                "IT", new BigDecimal("90000"), LocalDate.of(2021, 1, 1));
        
        employeeRepository.save(emp1);
        employeeRepository.save(emp2);
        employeeRepository.save(emp3);
        employeeRepository.save(emp4);
        Employee adminEmployee = employeeRepository.save(emp5);
        
        System.out.println("Sample employees created successfully!");
    }
    
    private void initializeUsers() {
        // Create admin user
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@company.com");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setRoles(Set.of(Role.ADMIN));
        adminUser.setEmployeeId(5L); // Assuming admin employee has ID 5
        adminUser.setEnabled(true);
        
        // Create regular employee user
        User employeeUser = new User();
        employeeUser.setUsername("john.doe");
        employeeUser.setEmail("john.doe@company.com");
        employeeUser.setPassword(passwordEncoder.encode("password123"));
        employeeUser.setRoles(Set.of(Role.EMPLOYEE));
        employeeUser.setEmployeeId(1L); // Assuming John Doe has ID 1
        employeeUser.setEnabled(true);
        
        userRepository.save(adminUser);
        userRepository.save(employeeUser);
        
        System.out.println("Sample users created successfully!");
        System.out.println("Admin credentials: admin / admin123");
        System.out.println("Employee credentials: john.doe / password123");
    }
}

