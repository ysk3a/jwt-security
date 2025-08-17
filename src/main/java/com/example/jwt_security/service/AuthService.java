package com.example.jwt_security.service;


import com.example.jwt_security.dto.*;
import com.example.jwt_security.entity.Token;
import com.example.jwt_security.entity.User;
import com.example.jwt_security.repository.TokenRepository;
import com.example.jwt_security.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private TokenRepository tokenRepository;

    // ?transactional?
    @Transactional
    public void registerUser(RegisterRequest registerRequest) {
        // check if user already exist. if exist than authenticate the user
        if(userRepository.existsByUsername(registerRequest.getUsername())) {
             throw new IllegalArgumentException("Username is already in use");
        }
        // Create new user
        User user = User
                .builder()
                .fullName(registerRequest.getFullName())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole())
                .build();
        userRepository.save(user);
    }

    public TokenPair login(LoginRequest loginRequest) {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Generate Token Pair
        TokenPair tokenPair = jwtService.generateTokenPair(authentication);
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();
        revokeAllTokenByUser(user);
        saveUserToken(tokenPair.getAccessToken(), tokenPair.getRefreshToken(), user);
        return tokenPair;
    }

    public ResponseEntity<?> refreshTokenWithoutAuth(
            HttpServletRequest request,
            HttpServletResponse response) {
        // extract the token from authorization header
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String token = authHeader.substring(7);
        if(!jwtService.isRefreshToken(token)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        // extract username from token
        String username = jwtService.extractUsernameFromToken(token);
        // check if the user exist in database
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("No user found"));
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        if (userDetails == null) {
            throw new IllegalArgumentException("User not found");
        }
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
        // check if the token is valid
        if(jwtService.validateTokenForUser(token, userDetails)) {
            // generate access token
            // Generate Token Pair
            TokenPair tokenPair = jwtService.generateTokenPair(authentication);
            // check if the user exist in database

            revokeAllTokenByUser(user);
            saveUserToken(tokenPair.getAccessToken(), tokenPair.getRefreshToken(), user);
            return new ResponseEntity<>(new AuthResponse(tokenPair.getAccessToken(), tokenPair.getRefreshToken(), "New token generated"), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    public TokenPair refreshToken(@Valid RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        // check if it is valid refresh token
        if(!jwtService.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        String user = jwtService.extractUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user);
        if (userDetails == null) {
            throw new IllegalArgumentException("User not found");
        }
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
        String accessToken = jwtService.generateAccessToken(authentication);
        return new TokenPair(accessToken, refreshToken);
    }

    private void revokeAllTokenByUser(User user) {
        List<Token> validTokens = tokenRepository.findAllAccessTokensByUser(user.getId());
        System.out.println("RevokeAllTokenByUser validToken=" + validTokens.isEmpty());
        if(validTokens.isEmpty()) {
            return;
        }
        validTokens.forEach(t-> {
            System.out.println("Token=" + t);
            t.setLoggedOut(true);
        });
        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(String accessToken, String refreshToken, User user) {
        Token token = new Token();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }

}