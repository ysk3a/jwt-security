package com.example.jwt_security.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenPair {
    private String accessToken;
    private String refreshToken;
}