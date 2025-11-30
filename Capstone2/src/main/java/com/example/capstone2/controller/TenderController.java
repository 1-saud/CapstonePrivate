package com.example.capstone2.controller;

import com.example.capstone2.API.ApiResponse;
import com.example.capstone2.model.Tender;
import com.example.capstone2.service.TenderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tenders")
public class TenderController {

    private final TenderService tenderService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllTenders() {
        List<Tender> tenders = tenderService.getAllTenders();
        return ResponseEntity.status(200).body(tenders);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getTenderById(@PathVariable Integer id) {
        Tender tender = tenderService.getTenderById(id);
        if (tender == null) {
            return ResponseEntity.status(400).body(new ApiResponse("Tender not found"));
        }
        return ResponseEntity.status(200).body(tender);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<?> getTendersByClient(@PathVariable Integer clientId) {
        List<Tender> tenders = tenderService.getTendersByClientId(clientId);
        return ResponseEntity.status(200).body(tenders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getTendersByStatus(@PathVariable String status) {
        List<Tender> tenders = tenderService.getTendersByStatus(status);
        return ResponseEntity.status(200).body(tenders);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addTender(@RequestBody @Valid Tender tender, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }

        tender.setCreatedAt(LocalDateTime.now());
        boolean isAdded = tenderService.addTender(tender);
        if (!isAdded) {
            return ResponseEntity.status(400).body(new ApiResponse("Could not add tender"));
        }

        return ResponseEntity.status(200).body(new ApiResponse("Tender added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTender(@PathVariable Integer id, @RequestBody @Valid Tender tender, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }

        boolean isUpdated = tenderService.updateTender(id, tender);
        if (!isUpdated) {
            return ResponseEntity.status(400).body(new ApiResponse("Tender not found"));
        }

        return ResponseEntity.status(200).body(new ApiResponse("Tender updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTender(@PathVariable Integer id) {
        boolean isDeleted = tenderService.deleteTender(id);
        if (!isDeleted) {
            return ResponseEntity.status(400).body(new ApiResponse("Tender not found"));
        }

        return ResponseEntity.status(200).body(new ApiResponse("Tender deleted successfully"));
    }
}
