package com.ariana.springboot.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UploadRecipeDto {
    private MultipartFile file;
    private RecipeDto request;

    public RecipeDto getRecipeDto() {
        return request;
    }
}
