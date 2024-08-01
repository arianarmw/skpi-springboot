package com.ariana.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ariana.springboot.entities.Level;

@Repository
public interface LevelRepository extends JpaRepository<Level, Integer> {
}
