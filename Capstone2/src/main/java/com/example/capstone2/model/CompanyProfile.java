package com.example.capstone2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CompanyProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "vendorId should not be null")
    @Column(nullable = false, unique = true)
    private Integer vendorId;    // FK -> User.id (VENDOR)

    @NotBlank(message = "company name should not be blank")
    @Size(min = 3, max = 200, message = "company name length must be between 3 and 200")
    @Column(nullable = false)
    private String companyName;

    @Column(columnDefinition = "text")
    private String summary;

    private String website;

    private String country;

    private String city;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
