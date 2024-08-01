package com.ariana.springboot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ariana.springboot.dto.ApiResponse;
import com.ariana.springboot.dto.SignInRequest;
import com.ariana.springboot.dto.SignUpRequest;
import com.ariana.springboot.services.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user-management/users")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse> signup(@RequestBody SignUpRequest signUpRequest) {
        ApiResponse response = authService.signup(signUpRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse> signin(@RequestBody SignInRequest signInRequest) {
        ApiResponse response = authService.signin(signInRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
