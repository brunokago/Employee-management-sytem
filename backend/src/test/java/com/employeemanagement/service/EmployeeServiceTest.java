package com.employeemanagement.service;

import com.employeemanagement.dto.EmployeeRequest;
import com.employeemanagement.entity.Employee;
import com.employeemanagement.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    
    @Mock
    private EmployeeRepository employeeRepository;
    
    @InjectMocks
    private EmployeeService employeeService;
    
    private Employee testEmployee;
    private EmployeeRequest testEmployeeRequest;
    
    @BeforeEach
    void setUp() {
        testEmployee = new Employee();
        testEmployee.setId(1L);
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setEmail("john.doe@company.com");
        testEmployee.setPhone("+1-555-0101");
        testEmployee.setDepartment("Engineering");
        testEmployee.setSalary(new BigDecimal("75000"));
        testEmployee.setDateOfJoining(LocalDate.of(2022, 1, 15));
        
        testEmployeeRequest = new EmployeeRequest();
        testEmployeeRequest.setFirstName("John");
        testEmployeeRequest.setLastName("Doe");
        testEmployeeRequest.setEmail("john.doe@company.com");
        testEmployeeRequest.setPhone("+1-555-0101");
        testEmployeeRequest.setDepartment("Engineering");
        testEmployeeRequest.setSalary(new BigDecimal("75000"));
        testEmployeeRequest.setDateOfJoining(LocalDate.of(2022, 1, 15));
    }
    
    @Test
    void testGetAllEmployees() {
        // Given
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeRepository.findAll()).thenReturn(employees);
        
        // When
        List<Employee> result = employeeService.getAllEmployees();
        
        // Then
        assertEquals(1, result.size());
        assertEquals(testEmployee, result.get(0));
        verify(employeeRepository, times(1)).findAll();
    }
    
    @Test
    void testGetEmployeeById() {
        // Given
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        
        // When
        Optional<Employee> result = employeeService.getEmployeeById(1L);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(testEmployee, result.get());
        verify(employeeRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetEmployeeByIdNotFound() {
        // Given
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When
        Optional<Employee> result = employeeService.getEmployeeById(1L);
        
        // Then
        assertFalse(result.isPresent());
        verify(employeeRepository, times(1)).findById(1L);
    }
    
    @Test
    void testCreateEmployee() {
        // Given
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        
        // When
        Employee result = employeeService.createEmployee(testEmployeeRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(testEmployee.getFirstName(), result.getFirstName());
        assertEquals(testEmployee.getLastName(), result.getLastName());
        assertEquals(testEmployee.getEmail(), result.getEmail());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }
    
    @Test
    void testUpdateEmployee() {
        // Given
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        
        // When
        Employee result = employeeService.updateEmployee(1L, testEmployeeRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(testEmployee.getFirstName(), result.getFirstName());
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }
    
    @Test
    void testUpdateEmployeeNotFound() {
        // Given
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When
        Employee result = employeeService.updateEmployee(1L, testEmployeeRequest);
        
        // Then
        assertNull(result);
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    void testDeleteEmployee() {
        // Given
        when(employeeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(employeeRepository).deleteById(1L);
        
        // When
        boolean result = employeeService.deleteEmployee(1L);
        
        // Then
        assertTrue(result);
        verify(employeeRepository, times(1)).existsById(1L);
        verify(employeeRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testDeleteEmployeeNotFound() {
        // Given
        when(employeeRepository.existsById(1L)).thenReturn(false);
        
        // When
        boolean result = employeeService.deleteEmployee(1L);
        
        // Then
        assertFalse(result);
        verify(employeeRepository, times(1)).existsById(1L);
        verify(employeeRepository, never()).deleteById(1L);
    }
    
    @Test
    void testSearchEmployees() {
        // Given
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeRepository.searchEmployees("John")).thenReturn(employees);
        
        // When
        List<Employee> result = employeeService.searchEmployees("John");
        
        // Then
        assertEquals(1, result.size());
        assertEquals(testEmployee, result.get(0));
        verify(employeeRepository, times(1)).searchEmployees("John");
    }
    
    @Test
    void testGetEmployeesByDepartment() {
        // Given
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeRepository.findByDepartment("Engineering")).thenReturn(employees);
        
        // When
        List<Employee> result = employeeService.getEmployeesByDepartment("Engineering");
        
        // Then
        assertEquals(1, result.size());
        assertEquals(testEmployee, result.get(0));
        verify(employeeRepository, times(1)).findByDepartment("Engineering");
    }
    
    @Test
    void testExistsByEmail() {
        // Given
        when(employeeRepository.existsByEmail("john.doe@company.com")).thenReturn(true);
        
        // When
        boolean result = employeeService.existsByEmail("john.doe@company.com");
        
        // Then
        assertTrue(result);
        verify(employeeRepository, times(1)).existsByEmail("john.doe@company.com");
    }
}

