package com.climbx.climbx.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final String FIXED_JWT_TOKEN;

    public JwtUtil(@Value("${auth.fixed-jwt}}") String FIXED_JWT_TOKEN) {
        this.FIXED_JWT_TOKEN = FIXED_JWT_TOKEN;
    }

    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    public String generateFixedToken() {
        // 고정된 임시 토큰 반환
        return FIXED_JWT_TOKEN;
    }

    public boolean validateToken(String token) {
        return FIXED_JWT_TOKEN.equals(token);
    }

    public Long extractSubject(String token) {
        if (validateToken(token)) {
            return 1L;
        }
        return null;
    }

    public String getFixedToken() {
        return FIXED_JWT_TOKEN;
    }
} 