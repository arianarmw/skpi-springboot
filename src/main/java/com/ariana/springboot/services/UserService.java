package com.ariana.springboot.services;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.ariana.springboot.entities.User;

public interface UserService {
    UserDetailsService userDetailsService();

    User findById(int userId);

    Integer getUserIdByUsername(String username);
}
