package com.yabozkurt.jwtauth.domain.exception;

public class MissingRefreshTokenException extends RuntimeException {
    public MissingRefreshTokenException() {
        super("Refresh token is missing");
    }
}
