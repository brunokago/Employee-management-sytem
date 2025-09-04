package com.employeemanagement.controller;

import com.employeemanagement.dto.ApiResponse;
import com.employeemanagement.dto.EmployeeRequest;
import com.employeemanagement.entity.Employee;
import com.employeemanagement.security.UserPrincipal;
import com.employeemanagement.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/employees")
@Tag(name = "Employee Management", description = "Employee CRUD operations")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {
    
    @Autowired
    private EmployeeService employeeService;
    
    @GetMapping
    @Operation(summary = "Get all employees")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('EMPLOYEE') and @employeeController.isOwnerOrAdmin(#id, authentication))")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id, Authentication authentication) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        if (employee.isPresent()) {
            return ResponseEntity.ok(employee.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    @Operation(summary = "Create new employee (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createEmployee(@Valid @RequestBody EmployeeRequest employeeRequest) {
        if (employeeService.existsByEmail(employeeRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Email is already in use!"));
        }
        
        try {
            Employee employee = employeeService.createEmployee(employeeRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Employee created successfully", employee));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to create employee: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update employee")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('EMPLOYEE') and @employeeController.isOwnerOrAdmin(#id, authentication))")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, 
                                          @Valid @RequestBody EmployeeRequest employeeRequest,
                                          Authentication authentication) {
        if (!employeeService.getEmployeeById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        if (employeeService.existsByEmailAndNotId(employeeRequest.getEmail(), id)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Email is already in use by another employee!"));
        }
        
        try {
            Employee updatedEmployee = employeeService.updateEmployee(id, employeeRequest);
            if (updatedEmployee != null) {
                return ResponseEntity.ok(new ApiResponse(true, "Employee updated successfully", updatedEmployee));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to update employee: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete employee (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        if (employeeService.deleteEmployee(id)) {
            return ResponseEntity.ok(new ApiResponse(true, "Employee deleted successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search employees")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<Employee>> searchEmployees(@RequestParam String q) {
        List<Employee> employees = employeeService.searchEmployees(q);
        return ResponseEntity.ok(employees);
    }
    
    @GetMapping("/department/{department}")
    @Operation(summary = "Get employees by department")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<Employee>> getEmployeesByDepartment(@PathVariable String department) {
        List<Employee> employees = employeeService.getEmployeesByDepartment(department);
        return ResponseEntity.ok(employees);
    }
    
    @GetMapping("/profile")
    @Operation(summary = "Get current user's employee profile")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<?> getCurrentUserProfile(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long employeeId = userPrincipal.getEmployeeId();
        
        if (employeeId != null) {
            Optional<Employee> employee = employeeService.getEmployeeById(employeeId);
            if (employee.isPresent()) {
                return ResponseEntity.ok(employee.get());
            }
        }
        
        return ResponseEntity.notFound().build();
    }
    
    // Helper method for authorization
    public boolean isOwnerOrAdmin(Long employeeId, Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        // Admin can access any employee
        if (userPrincipal.hasRole(com.employeemanagement.entity.Role.ADMIN)) {
            return true;
        }
        
        // Employee can only access their own profile
        return userPrincipal.getEmployeeId() != null && 
               userPrincipal.getEmployeeId().equals(employeeId);
    }
}

