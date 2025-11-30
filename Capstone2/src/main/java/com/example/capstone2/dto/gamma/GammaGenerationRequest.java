package com.example.capstone2.dto.gamma;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GammaGenerationRequest {

    private String inputText;

    private String textMode = "generate";  // generate | condense | preserve
    private String format = "presentation"; // slides
    private String exportAs = "pdf"; // pdf or pptx

    private GammaTextOptions textOptions;
    private GammaImageOptions imageOptions;

    private Integer numCards; // number of slides
    private String additionalInstructions;
}
