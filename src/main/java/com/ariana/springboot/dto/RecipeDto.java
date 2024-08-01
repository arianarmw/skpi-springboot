package com.ariana.springboot.dto;

import lombok.Data;

@Data
public class RecipeDto {
    private Integer userId;
    private Integer recipeId;
    private String recipeName;
    private LevelDto levels;
    private CategoryDto categories;
    private String howToCook;
    private Integer time;
    private String ingredient;
    private String imageUrl;
    private Boolean isFavorite;
    private Boolean isDeleted;
}
