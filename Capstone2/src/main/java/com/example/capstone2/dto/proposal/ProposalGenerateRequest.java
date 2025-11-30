package com.example.capstone2.dto.proposal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO used by /api/v1/proposals/generate
 * This is what your HTML page will send in the body.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProposalGenerateRequest {

    @NotNull(message = "vendorId should not be null")
    private Integer vendorId;     // current logged-in vendor (from localStorage)

    @NotNull(message = "tenderId should not be null")
    private Integer tenderId;     // selected tender from dropdown

    @NotBlank(message = "language should not be blank")
    private String language;      // "Arabic" or "English"

    // optional note the vendor writes before generating
    private String notes;
}
