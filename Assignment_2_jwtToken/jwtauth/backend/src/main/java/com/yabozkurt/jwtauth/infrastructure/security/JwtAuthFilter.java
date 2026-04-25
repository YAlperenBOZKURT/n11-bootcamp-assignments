package com.yabozkurt.jwtauth.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenManager jwtTokenManager;
    private final CustomUserDetailsService userDetailsService;
    private final CookieHelper cookieHelper;

    public JwtAuthFilter(JwtTokenManager jwtTokenManager,
                         CustomUserDetailsService userDetailsService,
                         CookieHelper cookieHelper) {
        this.jwtTokenManager = jwtTokenManager;
        this.userDetailsService = userDetailsService;
        this.cookieHelper = cookieHelper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = cookieHelper.readAccessToken(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtTokenManager.isTokenValid(token) && jwtTokenManager.isAccessToken(token)) {
                String email = jwtTokenManager.extractEmail(token);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception ignored) {
            // token geçersiz/expired: auth atlanır, endpoint korumalıysa spring 401 döner. 
        }

        filterChain.doFilter(request, response);
    }
}
