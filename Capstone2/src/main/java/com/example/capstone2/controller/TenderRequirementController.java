package com.example.capstone2.controller;


import com.example.capstone2.API.ApiResponse;
import com.example.capstone2.model.TenderRequirement;
import com.example.capstone2.service.TenderRequirementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tender-requirements")
public class TenderRequirementController {

    private final TenderRequirementService tenderRequirementService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllTenderRequirements() {
        List<TenderRequirement> requirements = tenderRequirementService.getAllTenderRequirements();
        return ResponseEntity.status(200).body(requirements);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getTenderRequirementById(@PathVariable Integer id) {
        TenderRequirement requirement = tenderRequirementService.getTenderRequirementById(id);
        if (requirement == null) {
            return ResponseEntity.status(400).body(new ApiResponse("Tender requirement not found"));
        }
        return ResponseEntity.status(200).body(requirement);
    }

    @GetMapping("/tender/{tenderId}")
    public ResponseEntity<?> getRequirementsByTenderId(@PathVariable Integer tenderId) {
        List<TenderRequirement> requirements =
                tenderRequirementService.getTenderRequirementsByTenderId(tenderId);
        return ResponseEntity.status(200).body(requirements);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addTenderRequirement(@RequestBody @Valid TenderRequirement requirement,
                                                  Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }

        boolean isAdded = tenderRequirementService.addTenderRequirement(requirement);
        if (!isAdded) {
            return ResponseEntity.status(400).body(new ApiResponse("Could not add tender requirement"));
        }

        return ResponseEntity.status(200).body(new ApiResponse("Tender requirement added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTenderRequirement(@PathVariable Integer id,
                                                     @RequestBody @Valid TenderRequirement requirement,
                                                     Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }

        boolean isUpdated = tenderRequirementService.updateTenderRequirement(id, requirement);
        if (!isUpdated) {
            return ResponseEntity.status(400).body(new ApiResponse("Tender requirement not found"));
        }

        return ResponseEntity.status(200).body(new ApiResponse("Tender requirement updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTenderRequirement(@PathVariable Integer id) {
        boolean isDeleted = tenderRequirementService.deleteTenderRequirement(id);
        if (!isDeleted) {
            return ResponseEntity.status(400).body(new ApiResponse("Tender requirement not found"));
        }

        return ResponseEntity.status(200).body(new ApiResponse("Tender requirement deleted successfully"));
    }
}