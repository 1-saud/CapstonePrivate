package com.example.capstone2.controller;

import com.example.capstone2.API.ApiResponse;
import com.example.capstone2.dto.auth.LoginRequest;
import com.example.capstone2.dto.auth.LoginResponse;
import com.example.capstone2.model.User;
import com.example.capstone2.repository.UserRepository;
import com.example.capstone2.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid User user, Errors errors) {

        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }


        boolean isAdded = userService.addUser(user);
        if (!isAdded) {
            return ResponseEntity.status(400).body(new ApiResponse("Could not register user"));
        }

        return ResponseEntity.status(200).body(new ApiResponse("User registered successfully"));
    }

    // 🔹 Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request, Errors errors) {

        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }

        User user = userRepository.findUserByEmailAndPassword(
                request.getEmail(),
                request.getPassword()
        );

        if (user == null) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse("Invalid email or password"));
        }

        LoginResponse response = new LoginResponse(
                user.getId(),
                user.getName(),
                user.getRole()
        );

        return ResponseEntity.status(200).body(response);
    }
}
