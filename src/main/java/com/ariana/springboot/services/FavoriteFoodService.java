package com.ariana.springboot.services;

import com.ariana.springboot.dto.ApiResponse;

public interface FavoriteFoodService {
    ApiResponse putFavoriteFood(int userId, int recipeId);
}
