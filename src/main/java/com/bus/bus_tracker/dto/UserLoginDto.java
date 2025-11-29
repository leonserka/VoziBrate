package com.bus.bus_tracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDto {

    @Email
    private String email;

    @NotBlank
    private String password;
}
