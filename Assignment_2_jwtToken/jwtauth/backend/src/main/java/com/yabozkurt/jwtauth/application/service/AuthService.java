package com.yabozkurt.jwtauth.application.service;

import com.yabozkurt.jwtauth.application.dto.LoginRequest;
import com.yabozkurt.jwtauth.application.dto.RegisterRequest;
import com.yabozkurt.jwtauth.application.dto.TokenInfoResponse;
import com.yabozkurt.jwtauth.application.dto.TokenResponse;

public interface AuthService {
    void register(RegisterRequest request);
    TokenResponse login(LoginRequest request);
    TokenResponse refresh(String refreshToken);
    TokenInfoResponse getTokenInfo(String accessToken, String refreshToken);
}