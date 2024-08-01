package com.ariana.springboot.dto;

import lombok.Data;

@Data
public class JwtAuthenticationResponse {

    private Integer id;
    private String token;
    private String refreshToken;
    private String type;
    private String username;
    private String role;
}
