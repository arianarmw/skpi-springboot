package com.ariana.springboot.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ariana.springboot.dto.ApiResponse;
import com.ariana.springboot.dto.CategoryDto;
import com.ariana.springboot.dto.FavoriteFoodDto;
import com.ariana.springboot.dto.LevelDto;
import com.ariana.springboot.dto.RecipeDto;
import com.ariana.springboot.dto.UploadRecipeDto;
import com.ariana.springboot.entities.Recipe;
import com.ariana.springboot.entities.User;
import com.ariana.springboot.services.CategoryService;
import com.ariana.springboot.services.FavoriteFoodService;
import com.ariana.springboot.services.LevelService;
import com.ariana.springboot.services.RecipeService;
import com.ariana.springboot.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private LevelService levelService;

    @Autowired
    private FavoriteFoodService favoriteFoodService;

    @Autowired
    private UserService userService;

    @GetMapping("/book-recipe/book-recipes")
    public ResponseEntity<Map<String, Object>> getAllRecipes(
            @RequestParam(name = "pageSize", defaultValue = "8") int pageSize,
            @RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber) {
        ApiResponse response = recipeService.getAllRecipes(pageNumber - 1, pageSize);
        long totalRecipes = recipeService.getTotalRecipes();

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", response.getData());
        responseBody.put("message", "Berhasil memuat Resep Masakan");
        responseBody.put("statusCode", 200);
        responseBody.put("status", "OK");
        responseBody.put("total", totalRecipes);

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/book-recipe/book-recipes/{id}")
    public ApiResponse getRecipeDetail(@PathVariable int id) {
        return recipeService.getRecipeDetail(id);
    }

    @GetMapping("/book-recipe-masters/category-option-lists")
    public ResponseEntity<Map<String, Object>> getAllCategories() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<CategoryDto> categories = categoryService.getAllCategories();
            response.put("data", categories);
            response.put("message", "Request was successful");
            response.put("statusCode", HttpStatus.OK.value());
            response.put("status", "OK");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("data", Collections.emptyList());
            response.put("message", "Request failed");
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("status", "FAIL");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/book-recipe-masters/level-option-lists")
    public ResponseEntity<Map<String, Object>> getAllLevels() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<LevelDto> levels = levelService.getAllLevels();
            response.put("data", levels);
            response.put("message", "Request was successful");
            response.put("statusCode", HttpStatus.OK.value());
            response.put("status", "OK");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("data", Collections.emptyList());
            response.put("message", "Request failed");
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("status", "FAIL");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/book-recipe/book-recipes/{recipeId}/favorites")
    public ResponseEntity<ApiResponse> putFavoriteFood(@RequestBody FavoriteFoodDto favoriteFoodDto,
            @PathVariable int recipeId) {
        ApiResponse response = favoriteFoodService.putFavoriteFood(favoriteFoodDto.getUserId(), recipeId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @PostMapping(value = "/book-recipe/book-recipes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> addRecipe(@ModelAttribute UploadRecipeDto uploadRecipeDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Map<String, Object> response = new HashMap<>();
        try {
            Recipe addedRecipe = recipeService.addRecipe(uploadRecipeDto.getRecipeDto(), user.getUserId());
            if (addedRecipe != null) {
                response.put("message", "Recipe added successfully");
                response.put("statusCode", HttpStatus.OK.value());
                response.put("status", "SUCCESS");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Failed to add recipe");
                response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.put("status", "FAIL");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            response.put("message", "Failed to add recipe");
            response.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("status", "FAIL");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/book-recipe/my-favorite-recipes")
    public ResponseEntity<Map<String, Object>> getFavoriteRecipes(
            @RequestParam(name = "userId", required = false) Integer userId,
            @RequestParam(name = "recipeName", required = false) String recipeName,
            @RequestParam(name = "levelId", required = false) Integer levelId,
            @RequestParam(name = "categoryId", required = false) Integer categoryId,
            @RequestParam(name = "time", required = false) Integer time,
            @RequestParam(name = "pageSize", defaultValue = "8") int pageSize,
            @RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber) {

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        ApiResponse response = recipeService.getFavoriteRecipes(user, recipeName, levelId, categoryId, time, pageable);

        List<RecipeDto> recipes = (List<RecipeDto>) response.getData();

        long totalRecipes = recipes.size();

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", recipes);
        responseBody.put("message", response.getMessage());
        responseBody.put("statusCode", response.getStatusCode());
        responseBody.put("status", response.getStatus());
        responseBody.put("total", totalRecipes);

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/book-recipe/my-recipes")
    public ResponseEntity<Map<String, Object>> getMyRecipes(
            @RequestParam(name = "pageSize", defaultValue = "8") int pageSize,
            @RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber) {
        ApiResponse response = recipeService.getMyRecipe(pageNumber - 1, pageSize);
        List<RecipeDto> recipes = (List<RecipeDto>) response.getData(); // Cast data

        long totalRecipes = recipes.size();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", response.getData()); // Add data to response body
        responseBody.put("message", response.getMessage());
        responseBody.put("statusCode", response.getStatusCode());
        responseBody.put("status", response.getStatus());
        responseBody.put("total", totalRecipes); // Add total outside of data

        return ResponseEntity.ok(responseBody);
    }

    // @PutMapping("/book-recipe/book-recipes/{recipeId}")
    // public ResponseEntity<Recipe> softDeleteRecipe(@PathVariable int recipeId,
    // Authentication authentication) {
    // String modifiedBy = authentication.getName(); // Assuming the username is
    // available here
    // Recipe updatedRecipe = recipeService.putDeleteRecipes(recipeId, modifiedBy);
    // return ResponseEntity.ok(updatedRecipe);
    // }

}
