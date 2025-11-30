package com.example.capstone2.service;

import com.example.capstone2.dto.gamma.GammaGenerationRequest;
import com.example.capstone2.dto.gamma.GammaGenerationStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GammaClientService {

    private final RestTemplate restTemplate;

    @Value("${gamma.api.key}")
    private String gammaApiKey;

    @Value("${gamma.api.url}")
    private String gammaApiUrl;

    // Step 1: Create generation (send input text)
    public String createGammaGeneration(GammaGenerationRequest request) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-KEY", gammaApiKey);

        HttpEntity<GammaGenerationRequest> entity =
                new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                gammaApiUrl,
                HttpMethod.POST,
                entity,
                Map.class
        );

        if (!response.getStatusCode().is2xxSuccessful()
                || response.getBody() == null
                || response.getBody().get("generationId") == null) {

            throw new RuntimeException("Gamma: Failed to create generation");
        }

        return response.getBody().get("generationId").toString();
    }

    // Step 2: Poll until completed
    public GammaGenerationStatusResponse waitForCompletion(String generationId) {

        String url = gammaApiUrl + "/" + generationId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", gammaApiKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        GammaGenerationStatusResponse lastStatus = null;

        int attempts = 0;
        while (attempts < 10) {   // نحاول 10 مرات، كل 5 ثواني
            attempts++;

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            Map<String, Object> body = response.getBody();

            if (body == null) {
                continue;
            }

            // نحفظ الرد الحالي
            lastStatus = new GammaGenerationStatusResponse();
            lastStatus.setRaw(body);

            Object s = body.get("status");
            if (s != null) {
                lastStatus.setStatus(s.toString());
            }

            Object gUrl = body.get("gammaUrl");
            if (gUrl != null) {
                lastStatus.setGammaUrl(gUrl.toString());
            }

            lastStatus.setGenerationId(generationId);

            // إذا خلص العمل نرجع فوراً
            if ("completed".equalsIgnoreCase(lastStatus.getStatus())) {
                return lastStatus;
            }

            // إذا ما خلص، ننتظر 5 ثواني ونعيد المحاولة
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
            }
        }

        // لو انتهت المحاولات بدون completed نرجع آخر حالة بدال ما نرمي Exception
        if (lastStatus != null) {
            return lastStatus;
        }

        // كحالة طوارئ لو ما جتنا أي استجابة مفيدة
        GammaGenerationStatusResponse fallback = new GammaGenerationStatusResponse();
        fallback.setStatus("unknown");
        fallback.setGenerationId(generationId);
        return fallback;
    }


    // Helper: send request + wait for response
    public GammaGenerationStatusResponse generateAndWait(GammaGenerationRequest request) {
        String id = createGammaGeneration(request);
        return waitForCompletion(id);
    }

}
