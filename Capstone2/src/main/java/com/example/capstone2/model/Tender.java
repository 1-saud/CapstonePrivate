package com.example.capstone2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Tender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "title should not be blank")
    @Size(min = 5, max = 200, message = "title length must be between 5 and 200")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "description should not be blank")
    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Min(value = 0, message = "minimum budget must be greater than or equal 0")
    private Integer budgetMin;

    @Min(value = 0, message = "maximum budget must be greater than or equal 0")
    private Integer budgetMax;

    @NotNull(message = "deadline should not be null")
    @Column(nullable = false)
    private LocalDate deadline;

    @NotBlank(message = "status should not be blank")
    @Pattern(regexp = "^(OPEN|CLOSED)$", message = "status must be OPEN, CLOSED ")
    @Column(nullable = false)
    private String status;   // OPEN / CLOSED

    @NotNull(message = "clientId should not be null")
    @Column(nullable = false)
    private Integer clientId;   // FK -> User.id (CLIENT)

    @Column(nullable = false)
    private LocalDateTime createdAt;
}