package com.ariana.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ariana.springboot.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
