package com.example.capstone2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TenderRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "tenderId should not be null")
    @Column(nullable = false)
    private Integer tenderId;    // FK -> Tender.id

    @NotBlank(message = "requirement text should not be blank")
    @Column(nullable = false, columnDefinition = "text")
    private String requirementText;

    @NotBlank(message = "priority should not be blank")
    @Pattern(regexp = "^(MUST_HAVE|NICE_TO_HAVE)$", message = "priority must be MUST_HAVE or NICE_TO_HAVE")
    @Column(nullable = false)
    private String priority;
}
