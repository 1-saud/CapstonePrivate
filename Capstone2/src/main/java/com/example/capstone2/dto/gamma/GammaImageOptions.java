package com.example.capstone2.dto.gamma;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GammaImageOptions {
    private String source; // aiGenerated | noImages | unsplash | ...
    private String style;  // professional, clean, Saudi-tech, ...
    private String model;  // optional - ignore for now
}
