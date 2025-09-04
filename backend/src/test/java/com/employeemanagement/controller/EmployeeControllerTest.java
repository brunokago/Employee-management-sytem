package com.employeemanagement.controller;

import com.employeemanagement.dto.EmployeeRequest;
import com.employeemanagement.entity.Employee;
import com.employeemanagement.entity.Role;
import com.employeemanagement.entity.User;
import com.employeemanagement.repository.EmployeeRepository;
import com.employeemanagement.repository.UserRepository;
import com.employeemanagement.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class EmployeeControllerTest {
    
    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    private String adminToken;
    private String employeeToken;
    private Employee testEmployee;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        
        // Create test employee
        testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setEmail("john.doe@test.com");
        testEmployee.setPhone("+1-555-0101");
        testEmployee.setDepartment("Engineering");
        testEmployee.setSalary(new BigDecimal("75000"));
        testEmployee.setDateOfJoining(LocalDate.of(2022, 1, 15));
        testEmployee = employeeRepository.save(testEmployee);
        
        // Create admin user
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@test.com");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setRoles(Set.of(Role.ADMIN));
        adminUser.setEnabled(true);
        adminUser = userRepository.save(adminUser);
        
        // Create employee user
        User empUser = new User();
        empUser.setUsername("john.doe");
        empUser.setEmail("john.doe@test.com");
        empUser.setPassword(passwordEncoder.encode("password123"));
        empUser.setRoles(Set.of(Role.EMPLOYEE));
        empUser.setEmployeeId(testEmployee.getId());
        empUser.setEnabled(true);
        empUser = userRepository.save(empUser);
        
        // Generate JWT tokens
        Authentication adminAuth = new UsernamePasswordAuthenticationToken(
                "admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        adminToken = jwtUtils.generateJwtToken(adminAuth);
        
        Authentication empAuth = new UsernamePasswordAuthenticationToken(
                "john.doe", null, List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));
        employeeToken = jwtUtils.generateJwtToken(empAuth);
    }
    
    @Test
    void testGetAllEmployees_WithAdminRole_ShouldReturnEmployees() throws Exception {
        mockMvc.perform(get("/employees")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }
    
    @Test
    void testGetAllEmployees_WithoutToken_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/employees"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    void testGetEmployeeById_WithAdminRole_ShouldReturnEmployee() throws Exception {
        mockMvc.perform(get("/employees/{id}", testEmployee.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }
    
    @Test
    void testCreateEmployee_WithAdminRole_ShouldCreateEmployee() throws Exception {
        EmployeeRequest newEmployee = new EmployeeRequest();
        newEmployee.setFirstName("Jane");
        newEmployee.setLastName("Smith");
        newEmployee.setEmail("jane.smith@test.com");
        newEmployee.setPhone("+1-555-0102");
        newEmployee.setDepartment("Marketing");
        newEmployee.setSalary(new BigDecimal("65000"));
        newEmployee.setDateOfJoining(LocalDate.of(2022, 3, 10));
        
        mockMvc.perform(post("/employees")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEmployee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Employee created successfully"));
    }
    
    @Test
    void testCreateEmployee_WithEmployeeRole_ShouldReturnForbidden() throws Exception {
        EmployeeRequest newEmployee = new EmployeeRequest();
        newEmployee.setFirstName("Jane");
        newEmployee.setLastName("Smith");
        newEmployee.setEmail("jane.smith@test.com");
        newEmployee.setDepartment("Marketing");
        newEmployee.setSalary(new BigDecimal("65000"));
        newEmployee.setDateOfJoining(LocalDate.of(2022, 3, 10));
        
        mockMvc.perform(post("/employees")
                .header("Authorization", "Bearer " + employeeToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEmployee)))
                .andExpect(status().isForbidden());
    }
    
    @Test
    void testUpdateEmployee_WithAdminRole_ShouldUpdateEmployee() throws Exception {
        EmployeeRequest updateRequest = new EmployeeRequest();
        updateRequest.setFirstName("John Updated");
        updateRequest.setLastName("Doe");
        updateRequest.setEmail("john.doe@test.com");
        updateRequest.setPhone("+1-555-0101");
        updateRequest.setDepartment("Engineering");
        updateRequest.setSalary(new BigDecimal("80000"));
        updateRequest.setDateOfJoining(LocalDate.of(2022, 1, 15));
        
        mockMvc.perform(put("/employees/{id}", testEmployee.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Employee updated successfully"));
    }
    
    @Test
    void testDeleteEmployee_WithAdminRole_ShouldDeleteEmployee() throws Exception {
        mockMvc.perform(delete("/employees/{id}", testEmployee.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Employee deleted successfully"));
    }
    
    @Test
    void testDeleteEmployee_WithEmployeeRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/employees/{id}", testEmployee.getId())
                .header("Authorization", "Bearer " + employeeToken))
                .andExpect(status().isForbidden());
    }
    
    @Test
    void testSearchEmployees_WithValidQuery_ShouldReturnResults() throws Exception {
        mockMvc.perform(get("/employees/search")
                .param("q", "John")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }
}

