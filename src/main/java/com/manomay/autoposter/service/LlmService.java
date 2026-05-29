package com.manomay.autoposter.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.Map;
import java.util.List;

@Service
public class LlmService {
    
    private final RestClient restClient = RestClient.create();
    
    @Value("${gemini.api.key}")
    private String apiKey;

    @SuppressWarnings("unchecked")
    public String generatePost(String message, String streamLink, String platform) {
        String prompt = buildPrompt(message, streamLink, platform);

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;
        
        Map<String, Object> requestBody = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", prompt)
                ))
            )
        );

        Map<String, Object> response = restClient.post()
                .uri(url)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        return (String) parts.get(0).get("text");
    }

    private String buildPrompt(String message, String streamLink, String platform) {
        return switch (platform) {
            case "TELEGRAM"  -> "Write a short Telegram message announcing a livestream. " +
                                "Message: " + message + ". Link: " + streamLink + ". Max 2 sentences, casual tone.";
            case "DISCORD"   -> "Write a Discord announcement for a livestream with bold text formatting. " +
                                "Message: " + message + ". Link: " + streamLink;
            default -> message + " " + streamLink;
        };
    }
}