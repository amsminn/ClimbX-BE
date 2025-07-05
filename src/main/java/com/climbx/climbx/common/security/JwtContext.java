package com.climbx.climbx.common.security;

import com.climbx.climbx.common.enums.RoleType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;

@Component
public class JwtContext {

    private final BearerTokenResolver bearerTokenResolver;
    private final String jwtSecret;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final String issuer;
    private final Key signingKey;

    public JwtContext(
        @Value("${auth.jwt.secret}") String jwtSecret,
        @Value("${auth.jwt.access-token-expiration}") long accessTokenExpiration,
        @Value("${auth.jwt.refresh-token-expiration}") long refreshTokenExpiration,
        @Value("${auth.jwt.issuer}") String issuer
    ) {
        // DefaultBearerTokenResolver 설정
        DefaultBearerTokenResolver resolver = new DefaultBearerTokenResolver();
        resolver.setAllowFormEncodedBodyParameter(false); // Form parameter 비활성화
        resolver.setAllowUriQueryParameter(false); // Query parameter 비활성화 (보안상 권장)
        this.bearerTokenResolver = resolver;

        this.jwtSecret = jwtSecret;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.issuer = issuer;
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Bearer 토큰 사용
     * Spring Security DefaultBearerTokenResolver 사용
     */
    public Optional<String> extractTokenFromRequest(HttpServletRequest request) {
        return Optional.ofNullable(bearerTokenResolver.resolve(request));
    }

    public String generateAccessToken(Long userId, String provider, RoleType role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiration * 1000);

        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .setIssuer(issuer)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .claim("provider", provider)
            .claim("role", role.name())
            .claim("type", "access")
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact();
    }

    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpiration * 1000);

        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .setIssuer(issuer)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .claim("type", "refresh")
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean validateToken(String token) {
        if (token == null) {
            return false;
        }

        try {
            Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 사용자 ID 추출
     */
    public Optional<Long> extractSubject(String token) {
        return extractClaims(token)
            .map(Claims::getSubject)
            .map(Long::parseLong);
    }

    /**
     * 토큰 타입을 추출합니다 (access 또는 refresh)
     */
    public Optional<String> getTokenType(String token) {
        return extractClaims(token)
            .map(claims -> claims.get("type", String.class));
    }

    /**
     * 토큰 Provider 추출
     */
    public Optional<String> getProvider(String token) {
        return extractClaims(token)
            .map(claims -> claims.get("provider", String.class));
    }

    /**
     * 토큰에서 사용자 역할을 추출합니다.
     *
     * @param token JWT 토큰
     * @return 사용자 역할 (Optional)
     */
    public Optional<RoleType> getRole(String token) {
        return extractClaims(token)
            .map(claims -> claims.get("role", String.class))
            .map(RoleType::valueOf);
    }

    /**
     * 토큰 Payload 추출
     */
    private Optional<Claims> extractClaims(String token) {
        if (token == null) {
            return Optional.empty();
        }

        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
            return Optional.of(claims);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }
} 