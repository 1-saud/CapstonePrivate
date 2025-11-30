package com.example.capstone2.dto.proposal;

import com.example.capstone2.model.Proposal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Simplified view model that combines proposal data with tender/vendor labels.
 * This keeps the frontend "history / review" pages easy to render without
 * needing many follow-up API calls for titles or names.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProposalViewDto {

    private Integer id;
    private Integer tenderId;
    private String tenderTitle;

    private Integer vendorId;
    private String vendorName;

    private String status;
    private String language;
    private String vendorNotes;
    private String submissionMessage;

    private String gammaShareLink;
    private String pdfUrl;
    private String pptxUrl;

    private String clientFeedback;

    private LocalDateTime submittedAt;
    private LocalDateTime decidedAt;
    private LocalDateTime createdAt;

    public static ProposalViewDto fromEntity(Proposal proposal) {
        ProposalViewDto dto = new ProposalViewDto();
        dto.setId(proposal.getId());
        dto.setTenderId(proposal.getTenderId());
        dto.setVendorId(proposal.getVendorId());
        dto.setStatus(proposal.getStatus());
        dto.setLanguage(proposal.getLanguage());
        dto.setVendorNotes(proposal.getVendorNotes());
        dto.setSubmissionMessage(proposal.getSubmissionMessage());
        dto.setGammaShareLink(proposal.getGammaShareLink());
        dto.setPdfUrl(proposal.getPdfUrl());
        dto.setPptxUrl(proposal.getPptxUrl());
        dto.setClientFeedback(proposal.getClientFeedback());
        dto.setSubmittedAt(proposal.getSubmittedAt());
        dto.setDecidedAt(proposal.getDecidedAt());
        dto.setCreatedAt(proposal.getCreatedAt());
        return dto;
    }
}
