package com.yabozkurt.jwtauth.presentation.controller;

import com.yabozkurt.jwtauth.application.dto.LoginRequest;
import com.yabozkurt.jwtauth.application.dto.RegisterRequest;
import com.yabozkurt.jwtauth.application.dto.TokenInfoResponse;
import com.yabozkurt.jwtauth.application.dto.TokenResponse;
import com.yabozkurt.jwtauth.application.service.AuthService;
import com.yabozkurt.jwtauth.infrastructure.security.CookieHelper;
import com.yabozkurt.jwtauth.presentation.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final CookieHelper cookieHelper;

    public AuthController(AuthService authService, CookieHelper cookieHelper) {
        this.authService = authService;
        this.cookieHelper = cookieHelper;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(@Valid @RequestBody LoginRequest request,
                                                   HttpServletResponse response) {
        TokenResponse tokenResponse = authService.login(request);
        cookieHelper.writeAuthCookies(response, tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Login successful", null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Void>> refresh(HttpServletRequest request,
                                                     HttpServletResponse response) {
        String refreshToken = cookieHelper.readRefreshToken(request);
        TokenResponse tokenResponse = authService.refresh(refreshToken);
        cookieHelper.writeAccessTokenCookie(response, tokenResponse.getAccessToken());
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", null));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        cookieHelper.clearCookies(response);
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }


    // request hits JwtAuthFilter first - if auth passes we reach here, otherwise spring returns 401
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Void>> me() {
        return ResponseEntity.ok(ApiResponse.success("OK", null));
    }

    @GetMapping("/token-info")
    public ResponseEntity<ApiResponse<TokenInfoResponse>> tokenInfo(HttpServletRequest request) {
        String accessToken = cookieHelper.readAccessToken(request);
        String refreshToken = cookieHelper.readRefreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(authService.getTokenInfo(accessToken, refreshToken)));
    }
}
