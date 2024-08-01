package com.ariana.springboot.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ariana.springboot.dto.LevelDto;
import com.ariana.springboot.entities.Level;
import com.ariana.springboot.repository.LevelRepository;
import com.ariana.springboot.services.LevelService;

@Service
public class LevelServiceImpl implements LevelService {

    private static final Logger logger = LoggerFactory.getLogger(LevelServiceImpl.class);

    @Autowired
    private LevelRepository levelRepository;

    // Method untuk mendapatkan list level
    @Override
    public List<LevelDto> getAllLevels() {
        List<Level> levels = levelRepository.findAll();
        logger.info("Number of levels retrieved: {}", levels.size()); // Cek
        return levels.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private LevelDto convertToDto(Level level) {
        LevelDto levelDto = new LevelDto();
        levelDto.setLevelId(level.getLevelId());
        levelDto.setLevelName(level.getLevelName());
        return levelDto;
    }
}
