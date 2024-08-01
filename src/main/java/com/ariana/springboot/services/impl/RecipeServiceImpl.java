package com.ariana.springboot.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ariana.springboot.dto.ApiResponse;
import com.ariana.springboot.dto.CategoryDto;
import com.ariana.springboot.dto.LevelDto;
import com.ariana.springboot.dto.RecipeDto;
import com.ariana.springboot.entities.Category;
import com.ariana.springboot.entities.FavoriteFood;
import com.ariana.springboot.entities.Level;
import com.ariana.springboot.entities.Recipe;
import com.ariana.springboot.entities.User;
import com.ariana.springboot.repository.CategoryRepository;
import com.ariana.springboot.repository.FavoriteFoodRepository;
import com.ariana.springboot.repository.LevelRepository;
import com.ariana.springboot.repository.RecipeRepository;
import com.ariana.springboot.repository.UserRepository;
import com.ariana.springboot.services.RecipeService;

@Service
public class RecipeServiceImpl implements RecipeService {

    private static final Logger logger = LoggerFactory.getLogger(RecipeServiceImpl.class);

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FavoriteFoodRepository favoriteFoodRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private MinioServiceImpl minioService;

    @Override
    public ApiResponse getAllRecipes(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "recipeName");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Recipe> recipesPage = recipeRepository.findAll(pageable);
        List<RecipeDto> recipeDtos = recipesPage.stream().map(this::convertToDto).collect(Collectors.toList());

        ApiResponse response = new ApiResponse();
        response.setData(recipeDtos);
        response.setMessage("Berhasil memuat Resep Masakan");
        response.setStatusCode(200);
        response.setStatus("OK");

        return response;
    }

    @Override
    public ApiResponse getRecipeDetail(int recipeId) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);

        ApiResponse response = new ApiResponse();
        if (recipeOptional.isPresent()) {
            RecipeDto recipeDto = convertToDto(recipeOptional.get());
            response.setData(recipeDto);
            response.setMessage("Berhasil memuat detail resep makanan");
            response.setStatusCode(200);
            response.setStatus("OK");
        } else {
            response.setMessage("Resep dengan ID " + recipeId + " tidak ditemukan");
            response.setStatusCode(404);
            response.setStatus("Not Found");
        }

        return response;
    }

    @Override
    public ApiResponse getFavoriteRecipes(User user, String recipeName, Integer levelId, Integer categoryId,
            Integer time, Pageable pageable) {
        Integer userId = user.getUserId();

        try {
            Page<Recipe> recipesPage = recipeRepository.findFavoriteRecipes(userId, pageable);
            List<RecipeDto> recipeDtos = recipesPage.stream().map(this::convertToDto).collect(Collectors.toList());

            ApiResponse response = new ApiResponse();
            response.setData(recipeDtos);
            response.setMessage("SUCCESS");
            response.setStatusCode(200);
            response.setStatus("OK");

            return response;
        } catch (Exception e) {
            ApiResponse response = new ApiResponse();
            response.setMessage("Error fetching favorite recipes");
            response.setStatusCode(500);
            response.setStatus("error");

            return response;
        }
    }

    @Override
    public ApiResponse getMyRecipe(int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Integer userId = userService.getUserIdByUsername(username);

        Sort sort = Sort.by(Sort.Direction.ASC, "recipeName");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Recipe> recipesPage = recipeRepository.findAllByUserId(userId, pageable);
        List<RecipeDto> recipeDtos = recipesPage.stream().map(this::convertToDto).collect(Collectors.toList());

        ApiResponse response = new ApiResponse();
        response.setData(recipeDtos);
        response.setMessage("Berhasil memuat Resep Masakan");
        response.setStatusCode(200);
        response.setStatus("OK");

        return response;
    }

    private RecipeDto convertToDto(Recipe recipe) {
        if (recipe == null) {
            return null;
        }

        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setRecipeId(recipe.getRecipeId());
        recipeDto.setRecipeName(recipe.getRecipeName());
        recipeDto.setIngredient(recipe.getIngredient());
        recipeDto.setHowToCook(recipe.getHowToCook());
        recipeDto.setTime(recipe.getTimeCook());
        recipeDto.setImageUrl(minioService.getPresignedUrl(recipe.getImageUrl()));

        if (recipe.getLevel() != null) {
            LevelDto levelDto = new LevelDto();
            levelDto.setLevelId(recipe.getLevel().getLevelId());
            levelDto.setLevelName(recipe.getLevel().getLevelName());
            recipeDto.setLevels(levelDto);
        }

        if (recipe.getCategory() != null) {
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setCategoryId(recipe.getCategory().getCategoryId());
            categoryDto.setCategoryName(recipe.getCategory().getCategoryName());
            recipeDto.setCategories(categoryDto);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        User activeUser = userRepository.findByUsername(currentPrincipalName)
                .orElseThrow(() -> new RuntimeException("User not found: " + currentPrincipalName));

        Optional<FavoriteFood> favoriteFoodOptional = favoriteFoodRepository.findByIdUserAndIdRecipe(activeUser,
                recipe);
        boolean isFavorite = favoriteFoodOptional.isPresent() && favoriteFoodOptional.get().isFavorite();
        recipeDto.setIsFavorite(isFavorite);

        return recipeDto;
    }

    @Override
    public Recipe addRecipe(RecipeDto recipeDto, int userId) {
        Recipe recipe = new Recipe();
        recipe.setRecipeName(recipeDto.getRecipeName());
        recipe.setHowToCook(recipeDto.getHowToCook());
        recipe.setTimeCook(recipeDto.getTime());
        recipe.setIngredient(recipeDto.getIngredient());
        recipe.setCreatedTime(LocalDateTime.now());
        recipe.setModifiedTime(LocalDateTime.now());
        recipe.setDeleted(false);

        User user = userRepository.findById(userId).orElse(null);
        recipe.setUser(user);

        LevelDto levelDto = recipeDto.getLevels();
        if (levelDto != null) {
            Level level = levelRepository.findById(levelDto.getLevelId()).orElse(null);
            recipe.setLevel(level);
        }

        CategoryDto categoryDto = recipeDto.getCategories();
        if (categoryDto != null) {
            Category category = categoryRepository.findById(categoryDto.getCategoryId()).orElse(null);
            recipe.setCategory(category);
        }

        String imageFilename = generateImageFilename(recipe.getRecipeName(), categoryDto, levelDto);
        recipe.setImageUrl(imageFilename);

        return recipeRepository.save(recipe);
    }

    private String generateImageFilename(String recipeName, CategoryDto categoryDto, LevelDto levelDto) {
        if (recipeName == null || categoryDto == null || levelDto == null) {
            return null;
        }
        String timestamp = String.valueOf(System.currentTimeMillis());
        return recipeName + "_" + categoryDto.getCategoryName() + "_" + levelDto.getLevelName() + "_" + timestamp;
    }

    @Override
    public long getTotalRecipes() {
        return recipeRepository.count();
    }

    @Override
    public Recipe findById(int recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));
    }
}
