package com.example.capstone2.dto.gamma;

import lombok.Data;

import java.util.Map;

@Data
public class GammaGenerationStatusResponse {

    private String status;
    private String generationId;
    private String gammaUrl;

    private Map<String, Object> raw; // store full response for now
}
