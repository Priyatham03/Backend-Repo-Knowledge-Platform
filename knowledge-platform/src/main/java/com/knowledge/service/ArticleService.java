package com.knowledge.service;

import com.knowledge.dto.ArticleDTO;
import com.knowledge.dto.CreateArticleRequest;
import com.knowledge.entity.Article;
import com.knowledge.entity.User;
import com.knowledge.repository.ArticleRepository;
import com.knowledge.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {
    
    @Autowired
    private ArticleRepository articleRepository;
    
    @Autowired
    private UserRepository userRepository;

    public ArticleDTO createArticle(Long userId, CreateArticleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Article article = new Article();
        article.setUser(user);
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setCategory(request.getCategory());
        article.setTags(request.getTags());
        article.setIsPublished(true);
        article.setSummary(generateSummary(request.getContent()));

        Article saved = articleRepository.save(article);
        return mapToDTO(saved);
    }

    public List<ArticleDTO> getAllPublishedArticles() {
        return articleRepository.findByIsPublishedTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ArticleDTO getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        return mapToDTO(article);
    }

    public List<ArticleDTO> getUserArticles(Long userId) {
        return articleRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ArticleDTO> searchArticles(String query) {
        return articleRepository.searchArticles(query)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ArticleDTO> filterByCategory(String category) {
        return articleRepository.findByCategoryAndIsPublishedTrue(category)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ArticleDTO updateArticle(Long articleId, Long userId, CreateArticleRequest request) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        if (!article.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: Only author can edit");
        }

        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setCategory(request.getCategory());
        article.setTags(request.getTags());
        article.setSummary(generateSummary(request.getContent()));

        Article updated = articleRepository.save(article);
        return mapToDTO(updated);
    }

    public void deleteArticle(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        if (!article.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: Only author can delete");
        }

        articleRepository.delete(article);
    }

    private String generateSummary(String content) {
        String cleanContent = content.replaceAll("<[^>]*>", "");
        if (cleanContent.length() > 150) {
            return cleanContent.substring(0, 150) + "...";
        }
        return cleanContent;
    }

    private ArticleDTO mapToDTO(Article article) {
        ArticleDTO dto = new ArticleDTO();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        dto.setSummary(article.getSummary());
        dto.setCategory(article.getCategory());
        dto.setTags(article.getTags());
        dto.setAuthorUsername(article.getUser().getUsername());
        dto.setAuthorId(article.getUser().getId());
        dto.setCreatedAt(article.getCreatedAt());
        dto.setUpdatedAt(article.getUpdatedAt());
        return dto;
    }
}