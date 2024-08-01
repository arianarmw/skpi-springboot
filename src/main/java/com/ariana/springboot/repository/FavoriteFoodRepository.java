package com.ariana.springboot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ariana.springboot.entities.FavoriteFood;
import com.ariana.springboot.entities.FavoriteFoodId;
import com.ariana.springboot.entities.Recipe;
import com.ariana.springboot.entities.User;

@Repository
public interface FavoriteFoodRepository extends JpaRepository<FavoriteFood, FavoriteFoodId> {

    Optional<FavoriteFood> findByIdUserAndIdRecipe(User user, Recipe recipe);
}
