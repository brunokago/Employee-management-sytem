package com.employeemanagement.entity;

public enum Role {
    ADMIN("ADMIN"),
    EMPLOYEE("EMPLOYEE");
    
    private final String value;
    
    Role(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
}

