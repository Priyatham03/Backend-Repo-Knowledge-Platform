package com.knowledge.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateArticleRequest {
    @NotBlank
    private String title;
    
    @NotBlank
    private String content;
    
    @NotBlank
    private String category;
    
    private String tags;
}