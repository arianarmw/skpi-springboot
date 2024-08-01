package com.ariana.springboot.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "recipes")
@Data
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private int recipeId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "level_id")
    private Level level;

    @Column(name = "recipe_name")
    private String recipeName;

    @Column(name = "image_filename")
    private String imageUrl;

    @Column(name = "time_cook")
    private int timeCook;

    @Column(name = "ingredient")
    private String ingredient;

    @Column(name = "how_to_cook")
    private String howToCook;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "modified_time")
    private LocalDateTime modifiedTime;
}
