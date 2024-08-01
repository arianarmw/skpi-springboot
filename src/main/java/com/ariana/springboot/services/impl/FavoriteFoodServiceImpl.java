package com.ariana.springboot.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ariana.springboot.dto.ApiResponse;
import com.ariana.springboot.entities.FavoriteFood;
import com.ariana.springboot.entities.FavoriteFoodId;
import com.ariana.springboot.entities.Recipe;
import com.ariana.springboot.entities.User;
import com.ariana.springboot.repository.FavoriteFoodRepository;
import com.ariana.springboot.repository.RecipeRepository;
import com.ariana.springboot.repository.UserRepository;
import com.ariana.springboot.services.FavoriteFoodService;

@Service
public class FavoriteFoodServiceImpl implements FavoriteFoodService {

    private static final Logger logger = LoggerFactory.getLogger(FavoriteFoodServiceImpl.class);

    private final RecipeRepository recipeRepository;
    private final FavoriteFoodRepository favoriteFoodRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public FavoriteFoodServiceImpl(RecipeRepository recipeRepository, FavoriteFoodRepository favoriteFoodRepository) {
        this.recipeRepository = recipeRepository;
        this.favoriteFoodRepository = favoriteFoodRepository;
    }

    @Override
    @Transactional
    public ApiResponse putFavoriteFood(int userId, int recipeId) {
        ApiResponse response = new ApiResponse();
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

            FavoriteFoodId id = new FavoriteFoodId(user, recipe);
            FavoriteFood favoriteFood = favoriteFoodRepository.findById(id).orElse(null);

            if (favoriteFood == null) {
                favoriteFood = new FavoriteFood();
                favoriteFood.setId(id);
                favoriteFood.setFavorite(true);
                favoriteFoodRepository.save(favoriteFood);
                response.setMessage("Recipe marked as favorite successfully");
            } else {
                favoriteFood.setFavorite(!favoriteFood.isFavorite());
                favoriteFoodRepository.save(favoriteFood);
                response.setMessage(favoriteFood.isFavorite() ? "Recipe marked as favorite successfully"
                        : "Recipe unmarked as favorite successfully");
            }

            response.setStatus("OK");
            response.setStatusCode(HttpStatus.OK.value());
            response.setTotal(1);
            response.setData(null);

        } catch (Exception e) {
            response.setStatus("error");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("An error occurred while marking recipe as favorite");
            response.setTotal(0);
            response.setData(null);
        }
        return response;
    }
}
