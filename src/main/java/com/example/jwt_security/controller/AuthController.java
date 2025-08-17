package com.example.jwt_security.controller;

import com.example.jwt_security.dto.*;
import com.example.jwt_security.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        // Save the new user to the database and return success response.
        authService.registerUser(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<?> refreshTokenWithoutAuth(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return authService.refreshTokenWithoutAuth(request, response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        TokenPair tokenPair = authService.login(loginRequest);
        return ResponseEntity.ok(tokenPair);
    }

//    @PostMapping("/refresh-token")
//    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
//        TokenPair tokenPair = authService.refreshToken(request);
//        return ResponseEntity.ok(tokenPair);
//    }
}
