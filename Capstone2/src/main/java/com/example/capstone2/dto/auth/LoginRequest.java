package com.example.capstone2.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "email should not be blank")
    @Email(message = "email must be valid")
    private String email;

    @NotBlank(message = "password should not be blank")
    private String password;
}
