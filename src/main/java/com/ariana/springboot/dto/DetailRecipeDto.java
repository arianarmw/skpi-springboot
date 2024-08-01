package com.ariana.springboot.dto;

import lombok.Data;

@Data
public class DetailRecipeDto {
    private Integer recipeId;
    private String recipeName;
    private LevelDto levels;
    private CategoryDto categories;
    private String howToCook;
    private Integer time;
    private String ingredient;
    private String imageFilename;
    private Boolean isFavorite;
}
