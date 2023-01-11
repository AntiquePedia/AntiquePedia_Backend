package com.example.antiquepedia_backend.info;

import lombok.Data;

@Data
public class LoginResponse {
    private String message;
    private Integer userId;
    private Integer role;
}
