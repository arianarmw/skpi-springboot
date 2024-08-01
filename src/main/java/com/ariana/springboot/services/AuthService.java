package com.ariana.springboot.services;

import com.ariana.springboot.dto.ApiResponse;
import com.ariana.springboot.dto.RefreshTokenRequest;
import com.ariana.springboot.dto.SignInRequest;
import com.ariana.springboot.dto.SignUpRequest;

public interface AuthService {
    ApiResponse signup(SignUpRequest signUpRequest);

    ApiResponse signin(SignInRequest signinRequest);

    ApiResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
