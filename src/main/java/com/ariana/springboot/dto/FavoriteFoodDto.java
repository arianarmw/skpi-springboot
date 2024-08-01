package com.ariana.springboot.dto;

import lombok.Data;

@Data
public class FavoriteFoodDto {
    private int userId;
    private CategoryDto categories;
    private LevelDto levels;
    private int time;
    private boolean isFavorite;
}
