package com.yabozkurt.jwtauth.application.dto;

public class TokenInfoResponse {

    private long accessTokenExpiresAt;
    private long refreshTokenExpiresAt;

    public TokenInfoResponse(long accessTokenExpiresAt, long refreshTokenExpiresAt) {
        this.accessTokenExpiresAt = accessTokenExpiresAt;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    }

    public long getAccessTokenExpiresAt() { return accessTokenExpiresAt; }
    public long getRefreshTokenExpiresAt() { return refreshTokenExpiresAt; }
}
