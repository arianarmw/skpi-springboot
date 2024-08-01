package com.ariana.springboot.services;

import org.springframework.data.domain.Pageable;

import com.ariana.springboot.dto.ApiResponse;
import com.ariana.springboot.dto.RecipeDto;
import com.ariana.springboot.entities.Recipe;
import com.ariana.springboot.entities.User;

public interface RecipeService {

        ApiResponse getAllRecipes(int page, int size);

        ApiResponse getRecipeDetail(int recipeId);

        Recipe addRecipe(RecipeDto recipeDto, int userId);

        long getTotalRecipes();

        Recipe findById(int recipeId);

        ApiResponse getFavoriteRecipes(User user, String recipeName, Integer levelId, Integer categoryId,
                        Integer time, Pageable pageable);

        ApiResponse getMyRecipe(int page, int size);

        // ApiResponse putDeletedRecipe(int recipeId, String modifiedBy);
}
