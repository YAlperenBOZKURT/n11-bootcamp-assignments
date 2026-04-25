package com.yabozkurt.jwtauth.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieHelper {

    private static final String ACCESS_TOKEN_COOKIE = "accessToken";
    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public void writeAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        response.addCookie(buildCookie(ACCESS_TOKEN_COOKIE, accessToken, (int) (accessTokenExpiration / 1000)));
        response.addCookie(buildCookie(REFRESH_TOKEN_COOKIE, refreshToken, (int) (refreshTokenExpiration / 1000)));
    }

    public void writeAccessTokenCookie(HttpServletResponse response, String accessToken) {
        response.addCookie(buildCookie(ACCESS_TOKEN_COOKIE, accessToken, (int) (accessTokenExpiration / 1000)));
    }

    public String readAccessToken(HttpServletRequest request) {
        return readCookie(request, ACCESS_TOKEN_COOKIE);
    }

    public String readRefreshToken(HttpServletRequest request) {
        return readCookie(request, REFRESH_TOKEN_COOKIE);
    }

    public void clearCookies(HttpServletResponse response) {
        response.addCookie(buildCookie(ACCESS_TOKEN_COOKIE, "", 0));
        response.addCookie(buildCookie(REFRESH_TOKEN_COOKIE, "", 0));
    }

    private String readCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private Cookie buildCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }

}
