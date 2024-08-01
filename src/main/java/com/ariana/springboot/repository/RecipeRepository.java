package com.ariana.springboot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ariana.springboot.entities.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Integer> {

        @Query("SELECT r FROM Recipe r " +
                        "JOIN FavoriteFood ff ON r.recipeId = ff.id.recipe.recipeId " +
                        "WHERE ff.id.user.userId = :userId " +
                        "AND ff.isFavorite = true " +
                        "AND r.isDeleted = FALSE " +
                        "ORDER BY r.recipeName ASC")
        Page<Recipe> findFavoriteRecipes(@Param("userId") Integer userId, Pageable pageable);

        @Query(value = "SELECT r FROM Recipe r WHERE r.user.userId = :userId AND r.isDeleted=FALSE")
        Page<Recipe> findAllByUserId(@Param("userId") Integer userId, Pageable pageable);

        // Optional<Recipe> findByRecipeIdAndUserId(int recipeId, int userId);
}
