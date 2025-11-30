package com.example.capstone2.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "name should not be blank")
    @Size(min = 3, max = 100, message = "name length must be between 3 and 100")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "email should not be blank")
    @Email(message = "email must be valid")
    @Column(nullable = false, unique = true)
    private String email;


    @NotBlank(message = "password should not be blank")
    @Size(min = 6, max = 100, message = "password must be at least 6 characters")
//    @Pattern(regexp = "^(CLIENT|VENDOR)$", message = "role must be either CLIENT or VENDOR")
    @Column(nullable = false)
    private String password;


    // CLIENT or VENDOR
    @NotBlank(message = "role should not be blank")
    @Column(nullable = false)
    private String role;


    @Column(nullable = false)
    private LocalDateTime createdAt;

}

