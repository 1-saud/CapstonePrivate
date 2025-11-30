package com.example.capstone2.dto.proposal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload used when a client approves / rejects / requests changes
 * for a submitted proposal.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProposalDecisionRequest {

    @NotNull(message = "clientId should not be null")
    private Integer clientId;    // FK -> User.id (CLIENT)

    @Size(max = 2000, message = "note is too long")
    private String note;         // optional client feedback shown to the vendor
}
