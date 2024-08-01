package com.ariana.springboot.dto;

import lombok.Data;

@Data
public class SignUpRequest {

    private String fullname;

    private String username;

    private String password;

    private String retypePassword;
}
