package com.example.capstone2.controller;

import com.example.capstone2.API.ApiResponse;
import com.example.capstone2.model.CompanyProfile;
import com.example.capstone2.service.CompanyProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/company-profiles")
public class CompanyProfileController {

    private final CompanyProfileService companyProfileService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllCompanyProfiles() {
        List<CompanyProfile> profiles = companyProfileService.getAllCompanyProfiles();
        return ResponseEntity.status(200).body(profiles);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getCompanyProfileById(@PathVariable Integer id) {
        CompanyProfile profile = companyProfileService.getCompanyProfileById(id);
        if (profile == null) {
            return ResponseEntity.status(400).body(new ApiResponse("Company profile not found"));
        }
        return ResponseEntity.status(200).body(profile);
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<?> getCompanyProfileByVendorId(@PathVariable Integer vendorId) {
        CompanyProfile profile = companyProfileService.getCompanyProfileByVendorId(vendorId);
        if (profile == null) {
            return ResponseEntity.status(400).body(new ApiResponse("Company profile not found"));
        }
        return ResponseEntity.status(200).body(profile);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCompanyProfile(@RequestBody @Valid CompanyProfile profile, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }

        profile.setCreatedAt(LocalDateTime.now());
        boolean isAdded = companyProfileService.addCompanyProfile(profile);
        if (!isAdded) {
            return ResponseEntity.status(400).body(new ApiResponse("This vendor already has a profile"));
        }

        return ResponseEntity.status(200).body(new ApiResponse("Company profile added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCompanyProfile(@PathVariable Integer id, @RequestBody @Valid CompanyProfile profile,
                                                  Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }

        boolean isUpdated = companyProfileService.updateCompanyProfile(id, profile);
        if (!isUpdated) {
            return ResponseEntity.status(400).body(new ApiResponse("Company profile not found"));
        }

        return ResponseEntity.status(200).body(new ApiResponse("Company profile updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCompanyProfile(@PathVariable Integer id) {
        boolean isDeleted = companyProfileService.deleteCompanyProfile(id);
        if (!isDeleted) {
            return ResponseEntity.status(400).body(new ApiResponse("Company profile not found"));
        }

        return ResponseEntity.status(200).body(new ApiResponse("Company profile deleted successfully"));
    }
}
