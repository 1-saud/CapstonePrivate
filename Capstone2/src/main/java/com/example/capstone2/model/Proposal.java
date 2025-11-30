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
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "tenderId should not be null")
    @Column(nullable = false)
    private Integer tenderId;    // FK -> Tender.id

    @NotNull(message = "vendorId should not be null")
    @Column(nullable = false)
    private Integer vendorId;    // FK -> User.id (VENDOR)

    @NotBlank(message = "status should not be blank")
    @Pattern(regexp = "^(APPROVED|REJECTED|NEEDS_CHANGES)$", message = "APPROVED, REJECTED, or NEEDS_CHANGES"
    )
    @Column(nullable = false)
    private String status;       // APPROVED / REJECTED / NEEDS_CHANGES

    @Column(columnDefinition = "text")
    private String technicalSummary;

    @Column(columnDefinition = "text")
    private String financialSummary;

    private String cerebrasRequestId;

    private String gammaShareLink;

    private String pdfUrl;

    private String pptxUrl;

    private String aiProvider;   // CEREBRAS / OPENAI / ...

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

}
