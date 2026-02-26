package com.knowledge.controller;

import com.knowledge.dto.ArticleDTO;
import com.knowledge.dto.CreateArticleRequest;
import com.knowledge.repository.UserRepository;
import com.knowledge.security.JwtTokenProvider;
import com.knowledge.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/articles")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ArticleController {
    
    @Autowired
    private ArticleService articleService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<ArticleDTO>> getAllArticles() {
        List<ArticleDTO> articles = articleService.getAllPublishedArticles();
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable Long id) {
        try {
            ArticleDTO article = articleService.getArticleById(id);
            return ResponseEntity.ok(article);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ArticleDTO>> getUserArticles(@PathVariable Long userId) {
        List<ArticleDTO> articles = articleService.getUserArticles(userId);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ArticleDTO>> searchArticles(@RequestParam String q) {
        List<ArticleDTO> articles = articleService.searchArticles(q);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ArticleDTO>> filterByCategory(@PathVariable String category) {
        List<ArticleDTO> articles = articleService.filterByCategory(category);
        return ResponseEntity.ok(articles);
    }

    @PostMapping
    public ResponseEntity<?> createArticle(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateArticleRequest request) {
        try {
            String token = extractToken(authHeader);
            String email = tokenProvider.getEmailFromToken(token);
            Long userId = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();
            
            ArticleDTO article = articleService.createArticle(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(article);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateArticle(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateArticleRequest request) {
        try {
            String token = extractToken(authHeader);
            String email = tokenProvider.getEmailFromToken(token);
            Long userId = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();
            
            ArticleDTO article = articleService.updateArticle(id, userId, request);
            return ResponseEntity.ok(article);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            String email = tokenProvider.getEmailFromToken(token);
            Long userId = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();
            
            articleService.deleteArticle(id, userId);
            return ResponseEntity.ok(Map.of("message", "Article deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("Invalid authorization header");
    }
}