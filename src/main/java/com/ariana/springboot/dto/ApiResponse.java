package com.ariana.springboot.dto;

import lombok.Data;

@Data
public class ApiResponse {
    private Object data;
    private String message;
    private int statusCode;
    private String status;
    private long total;
}
