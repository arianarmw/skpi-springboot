package com.ariana.springboot.services;

import java.util.List;

import com.ariana.springboot.dto.CategoryDto;

public interface CategoryService {
    List<CategoryDto> getAllCategories();
}
