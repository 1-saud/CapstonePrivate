package com.example.capstone2.controller;

import com.example.capstone2.API.ApiResponse;
import com.example.capstone2.dto.proposal.ProposalDecisionRequest;
import com.example.capstone2.dto.proposal.ProposalGenerateRequest;
import com.example.capstone2.dto.proposal.ProposalSubmitRequest;
import com.example.capstone2.dto.proposal.ProposalViewDto;
import com.example.capstone2.model.Proposal;
import com.example.capstone2.service.ProposalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/proposals")
public class ProposalController {

    private final ProposalService proposalService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllProposals() {
        List<Proposal> proposals = proposalService.getAllProposals();
        return ResponseEntity.status(200).body(proposals);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getProposalById(@PathVariable Integer id) {
        Proposal proposal = proposalService.getProposalById(id);
        if (proposal == null) {
            return ResponseEntity.status(400).body(new ApiResponse("Proposal not found"));
        }
        return ResponseEntity.status(200).body(proposal);
    }

    @GetMapping("/tender/{tenderId}")
    public ResponseEntity<?> getProposalsByTenderId(@PathVariable Integer tenderId) {
        List<Proposal> proposals = proposalService.getProposalsByTenderId(tenderId);
        return ResponseEntity.status(200).body(proposals);
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<?> getProposalsByVendorId(@PathVariable Integer vendorId) {
        List<Proposal> proposals = proposalService.getProposalsByVendorId(vendorId);
        return ResponseEntity.status(200).body(proposals);
    }

    @GetMapping("/vendor/{vendorId}/view")
    public ResponseEntity<?> getProposalViewsForVendor(@PathVariable Integer vendorId) {
        List<ProposalViewDto> proposals = proposalService.getProposalViewsForVendor(vendorId);
        return ResponseEntity.status(200).body(proposals);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getProposalsByStatus(@PathVariable String status) {
        List<Proposal> proposals = proposalService.getProposalsByStatus(status);
        return ResponseEntity.status(200).body(proposals);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<?> getProposalsForClient(@PathVariable Integer clientId) {
        List<Proposal> proposals = proposalService.getProposalsForClient(clientId);
        return ResponseEntity.status(200).body(proposals);
    }

    @GetMapping("/client/{clientId}/view")
    public ResponseEntity<?> getProposalViewsForClient(@PathVariable Integer clientId) {
        List<ProposalViewDto> proposals = proposalService.getProposalViewsForClient(clientId);
        return ResponseEntity.status(200).body(proposals);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProposal(@RequestBody @Valid Proposal proposal,
                                         Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }

        LocalDateTime now = LocalDateTime.now();
        proposal.setCreatedAt(now);
        proposal.setUpdatedAt(now);

        boolean isAdded = proposalService.addProposal(proposal);
        if (!isAdded) {
            return ResponseEntity.status(400).body(new ApiResponse("Could not add proposal"));
        }

        return ResponseEntity.status(200).body(new ApiResponse("Proposal added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProposal(@PathVariable Integer id,
                                            @RequestBody @Valid Proposal proposal,
                                            Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }

        proposal.setUpdatedAt(LocalDateTime.now());

        boolean isUpdated = proposalService.updateProposal(id, proposal);
        if (!isUpdated) {
            return ResponseEntity.status(400).body(new ApiResponse("Proposal not found"));
        }

        return ResponseEntity.status(200).body(new ApiResponse("Proposal updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProposal(@PathVariable Integer id) {
        boolean isDeleted = proposalService.deleteProposal(id);
        if (!isDeleted) {
            return ResponseEntity.status(400).body(new ApiResponse("Proposal not found"));
        }

        return ResponseEntity.status(200).body(new ApiResponse("Proposal deleted successfully"));
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody @Valid ProposalGenerateRequest request,
                                      Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }

        try {
            Proposal proposal = proposalService.generateProposal(request);
            return ResponseEntity.status(200).body(proposal);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(400).body(new ApiResponse(ex.getMessage()));
        }
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitProposal(@PathVariable Integer id,
                                            @RequestBody @Valid ProposalSubmitRequest request,
                                            Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }

        try {
            Proposal proposal = proposalService.submitProposal(id, request);
            return ResponseEntity.status(200).body(proposal);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(400).body(new ApiResponse(ex.getMessage()));
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveProposal(@PathVariable Integer id,
                                             @RequestBody @Valid ProposalDecisionRequest request,
                                             Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }

        try {
            Proposal proposal = proposalService.approveProposal(id, request);
            return ResponseEntity.status(200).body(proposal);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(400).body(new ApiResponse(ex.getMessage()));
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectProposal(@PathVariable Integer id,
                                            @RequestBody @Valid ProposalDecisionRequest request,
                                            Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }

        try {
            Proposal proposal = proposalService.rejectProposal(id, request);
            return ResponseEntity.status(200).body(proposal);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(400).body(new ApiResponse(ex.getMessage()));
        }
    }

    @PostMapping("/{id}/request-changes")
    public ResponseEntity<?> requestChanges(@PathVariable Integer id,
                                            @RequestBody @Valid ProposalDecisionRequest request,
                                            Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }

        try {
            Proposal proposal = proposalService.requestChanges(id, request);
            return ResponseEntity.status(200).body(proposal);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(400).body(new ApiResponse(ex.getMessage()));
        }
    }
}
