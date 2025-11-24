package com.bus.bus_tracker.dto;

public class UserResponseDto {
    private String name;
    private String email;
    private String role;

    public UserResponseDto(String name, String email, String role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
