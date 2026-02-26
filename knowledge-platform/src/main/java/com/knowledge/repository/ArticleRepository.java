package com.knowledge.repository;

import com.knowledge.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByIsPublishedTrue();
    List<Article> findByIsPublishedTrueOrderByCreatedAtDesc();
    List<Article> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Article> findByCategoryAndIsPublishedTrue(String category);
    
    @Query("SELECT a FROM Article a WHERE a.isPublished = true AND " +
           "(LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.tags) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Article> searchArticles(@Param("search") String search);
}