package com.example.capstone2.dto.gamma;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GammaTextOptions {
    private String amount;    // brief | medium | detailed | extensive
    private String tone;      // professional, formal, ...
    private String audience;  // saudi government procurement officers ...
    private String language;  // "ar" أو "en"
}
