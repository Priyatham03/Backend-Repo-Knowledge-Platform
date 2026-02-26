package com.knowledge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AiController {

    @Value("${anthropic.api.key}")
    private String anthropicApiKey;

    // Use the shared RestTemplate bean from SecurityConfig
    @Autowired
    private RestTemplate restTemplate;

    private static final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String CLAUDE_MODEL    = "claude-sonnet-4-20250514";

    /**
     * POST /api/ai/claude
     *
     * Receives: { "systemPrompt": "...", "userPrompt": "...", "maxTokens": 1000 }
     * Returns:  { "text": "..." }
     *
     * Acts as a secure proxy — the Anthropic API key never leaves the server.
     */
    @PostMapping("/claude")
    public ResponseEntity<?> proxyToClaude(@RequestBody Map<String, Object> body) {
        try {
            String systemPrompt = (String) body.get("systemPrompt");
            String userPrompt   = (String) body.get("userPrompt");
            int maxTokens       = body.containsKey("maxTokens")
                                    ? (int) body.get("maxTokens") : 1000;

            if (systemPrompt == null || systemPrompt.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "systemPrompt is required"));
            }
            if (userPrompt == null || userPrompt.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "userPrompt is required"));
            }

            // Build the request body for Anthropic
            Map<String, Object> claudeRequest = Map.of(
                "model",      CLAUDE_MODEL,
                "max_tokens", maxTokens,
                "system",     systemPrompt,
                "messages",   List.of(Map.of("role", "user", "content", userPrompt))
            );

            // Required Anthropic headers — added server-side so the key stays secret
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key",         anthropicApiKey);
            headers.set("anthropic-version", "2023-06-01");

            ResponseEntity<Map> claudeResponse = restTemplate.postForEntity(
                CLAUDE_API_URL,
                new HttpEntity<>(claudeRequest, headers),
                Map.class
            );

            // Extract the text block from Anthropic's response
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> content =
                (List<Map<String, Object>>) claudeResponse.getBody().get("content");

            String text = content.stream()
                .filter(block -> "text".equals(block.get("type")))
                .map(block -> (String) block.get("text"))
                .findFirst()
                .orElse("");

            return ResponseEntity.ok(Map.of("text", text));

        } catch (HttpClientErrorException e) {
            // Anthropic returned 4xx — surface the actual error message
            return ResponseEntity.status(e.getStatusCode())
                .body(Map.of("error", "Anthropic API error: " + e.getResponseBodyAsString()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "AI request failed: " + e.getMessage()));
        }
    }
}