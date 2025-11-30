package com.example.capstone2.controller;

import com.example.capstone2.API.ApiResponse;
import com.example.capstone2.dto.gamma.GammaGenerationRequest;
import com.example.capstone2.dto.gamma.GammaGenerationStatusResponse;
import com.example.capstone2.service.GammaClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gamma")
public class GammaController {

    private final GammaClientService gammaClientService;

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody @Valid GammaGenerationRequest request,
                                      Errors errors) {

        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }

        try {
            GammaGenerationStatusResponse result =
                    gammaClientService.generateAndWait(request);

            return ResponseEntity.status(200).body(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(new ApiResponse("Gamma generation failed: " + e.getMessage()));
        }
    }
}
