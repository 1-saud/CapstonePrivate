package com.example.capstone2.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private Integer id;
    private String name;
    private String role;
}
