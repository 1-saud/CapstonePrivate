package com.example.capstone2.dto.proposal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload used when a vendor submits an already generated proposal
 * to the client for review.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProposalSubmitRequest {

    @NotNull(message = "vendorId should not be null")
    private Integer vendorId;    // FK -> User.id (VENDOR)

    @Size(max = 2000, message = "message is too long")
    private String message;      // optional vendor message for the client
}
