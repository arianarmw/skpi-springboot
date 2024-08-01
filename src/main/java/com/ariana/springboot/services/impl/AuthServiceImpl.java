package com.ariana.springboot.services.impl;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ariana.springboot.dto.ApiResponse;
import com.ariana.springboot.dto.JwtAuthenticationResponse;
import com.ariana.springboot.dto.RefreshTokenRequest;
import com.ariana.springboot.dto.SignInRequest;
import com.ariana.springboot.dto.SignUpRequest;
import com.ariana.springboot.entities.Role;
import com.ariana.springboot.entities.User;
import com.ariana.springboot.repository.UserRepository;
import com.ariana.springboot.services.AuthService;
import com.ariana.springboot.services.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    public ApiResponse signup(SignUpRequest signUpRequest) {
        ApiResponse apiResponse = new ApiResponse();

        if (!signUpRequest.getPassword().equals(signUpRequest.getRetypePassword())) {
            apiResponse.setMessage("Passwords do not match");
            apiResponse.setStatusCode(400);
            apiResponse.setStatus("failure");
            return apiResponse;
        }

        if (userRepository.findByUsername(signUpRequest.getUsername()).isPresent()) {
            apiResponse.setMessage("Username already exists");
            apiResponse.setStatusCode(400);
            apiResponse.setStatus("failure");
            return apiResponse;
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setFullname(signUpRequest.getFullname());

        String roleValue = Role.USER.name();
        String formattedRoleValue = roleValue.substring(0, 1).toUpperCase() + roleValue.substring(1).toLowerCase();
        logger.info("Role value: {}", formattedRoleValue);
        user.setRole(formattedRoleValue);

        user.setDeleted(false);
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        LocalDateTime currentTime = LocalDateTime.now();
        user.setCreatedTime(currentTime);

        userRepository.save(user);

        apiResponse.setMessage("User " + signUpRequest.getUsername() + " registered successfully!");
        apiResponse.setStatusCode(201);
        apiResponse.setStatus("success");

        return apiResponse;
    }

    public ApiResponse signin(SignInRequest signinRequest) {
        logger.info("Received sign-in request with username: {}", signinRequest.getUsername());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getUsername(),
                signinRequest.getPassword()));

        var user = userRepository.findByUsername(signinRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        var jwt = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();

        jwtAuthenticationResponse.setId(user.getUserId());
        jwtAuthenticationResponse.setToken(jwt);
        jwtAuthenticationResponse.setRefreshToken(refreshToken);
        jwtAuthenticationResponse.setType("Bearer");
        jwtAuthenticationResponse.setUsername(user.getUsername());
        jwtAuthenticationResponse.setRole(user.getRole());

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setData(jwtAuthenticationResponse);
        apiResponse.setMessage("Login successful");
        apiResponse.setStatusCode(200);
        apiResponse.setStatus("success");

        return apiResponse;
    }

    public ApiResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String username = jwtService.extractUserName(refreshTokenRequest.getToken());

        logger.info("Refresh token received: {}", refreshTokenRequest.getToken());
        User user = userRepository.findByUsername(username).orElseThrow();
        if (jwtService.isTokenValid(refreshTokenRequest.getToken(), user)) {
            var jwt = jwtService.generateToken(user);

            JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();

            jwtAuthenticationResponse.setId(user.getUserId());
            jwtAuthenticationResponse.setToken(jwt);
            jwtAuthenticationResponse.setRefreshToken(refreshTokenRequest.getToken());
            jwtAuthenticationResponse.setType("Bearer");
            jwtAuthenticationResponse.setUsername(user.getUsername());
            jwtAuthenticationResponse.setRole(user.getRole());

            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setData(jwtAuthenticationResponse);
            apiResponse.setMessage("Token refreshed successfully");
            apiResponse.setStatusCode(200);
            apiResponse.setStatus("success");

            return apiResponse;
        }

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Invalid refresh token");
        apiResponse.setStatusCode(401);
        apiResponse.setStatus("failure");
        return apiResponse;
    }

}
