package com.yabozkurt.jwtauth.application.service.impl;

import com.yabozkurt.jwtauth.application.dto.LoginRequest;
import com.yabozkurt.jwtauth.application.dto.RegisterRequest;
import com.yabozkurt.jwtauth.application.dto.TokenInfoResponse;
import com.yabozkurt.jwtauth.application.dto.TokenResponse;
import com.yabozkurt.jwtauth.application.service.AuthService;
import com.yabozkurt.jwtauth.domain.exception.InvalidRefreshTokenException;
import com.yabozkurt.jwtauth.domain.exception.MissingRefreshTokenException;
import com.yabozkurt.jwtauth.domain.exception.UserAlreadyExistsException;
import com.yabozkurt.jwtauth.domain.model.User;
import com.yabozkurt.jwtauth.domain.model.enums.Role;
import com.yabozkurt.jwtauth.domain.repository.UserRepository;
import com.yabozkurt.jwtauth.infrastructure.security.JwtTokenManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenManager jwtTokenManager;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenManager jwtTokenManager,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenManager = jwtTokenManager;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.USER
        );

        userRepository.save(user);
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String accessToken = jwtTokenManager.generateAccessToken(request.getEmail());
        String refreshToken = jwtTokenManager.generateRefreshToken(request.getEmail());

        return new TokenResponse(accessToken, refreshToken);
    }

    @Override
    public TokenResponse refresh(String refreshToken) {
        if (refreshToken == null) {
            throw new MissingRefreshTokenException();
        }

        if (!jwtTokenManager.isTokenValid(refreshToken) || !jwtTokenManager.isRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        String email = jwtTokenManager.extractEmail(refreshToken);
        String newAccessToken = jwtTokenManager.generateAccessToken(email);
        return new TokenResponse(newAccessToken, refreshToken);
    }

    @Override
    public TokenInfoResponse getTokenInfo(String accessToken, String refreshToken) {
        long accessExp = jwtTokenManager.extractExpiration(accessToken);
        long refreshExp = jwtTokenManager.extractExpiration(refreshToken);
        return new TokenInfoResponse(accessExp, refreshExp);
    }
}