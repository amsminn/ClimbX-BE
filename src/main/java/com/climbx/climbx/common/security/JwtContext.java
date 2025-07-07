package com.climbx.climbx.common.security;

import com.climbx.climbx.common.enums.RoleType;
<<<<<<< HEAD
import com.climbx.climbx.common.enums.TokenType;
import com.climbx.climbx.common.security.exception.InvalidTokenException;
import com.climbx.climbx.common.security.exception.TokenExpiredException;
import com.climbx.climbx.common.util.OptionalUtils;
=======
import com.climbx.climbx.common.security.exception.InvalidTokenException;
import com.climbx.climbx.common.security.exception.TokenExpiredException;
>>>>>>> 8947ec5 (refactor: 인증 관련 DTO, 예외 처리, JWT 필터 및 테스트 코드 리팩토링)
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
     * Bearer 토큰 사용 Spring Security DefaultBearerTokenResolver 사용
     */
    public String extractTokenFromRequest(HttpServletRequest request) {
        return Optional.ofNullable(bearerTokenResolver.resolve(request))
            .orElseThrow(
                () -> new InvalidTokenException("Bearer token not found in request header"));
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
            .claim("type", TokenType.ACCESS.name())
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
            .claim("type", TokenType.REFRESH.name())
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact();
    }

<<<<<<< HEAD
    /**
     * 토큰 Payload 추출
     */
    private Claims extractClaims(String token) {
        if (token == null) {
            throw new InvalidTokenException("Token is null");
=======
    public void validateToken(String token) {
        if (token == null) {
            throw new InvalidTokenException("토큰이 존재하지 않습니다.");
>>>>>>> 8947ec5 (refactor: 인증 관련 DTO, 예외 처리, JWT 필터 및 테스트 코드 리팩토링)
        }

        try {
            return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
<<<<<<< HEAD
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (Exception e) {
            throw new InvalidTokenException();
=======
                .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (Exception e) {
            throw new InvalidTokenException("유효하지 않은 토큰입니다: " + e.getMessage());
>>>>>>> 8947ec5 (refactor: 인증 관련 DTO, 예외 처리, JWT 필터 및 테스트 코드 리팩토링)
        }

    }

    /**
     * 사용자 ID 추출
     */
    public Long extractSubject(String token) {
        return OptionalUtils.tryOf(
                () -> {
                    Claims claims = extractClaims(token);
                    String subject = claims.getSubject();
                    return Long.parseLong(subject);
                }
            )
            .orElseThrow(() -> new InvalidTokenException("user id not found in payload"));
    }

    /**
     * 토큰 타입을 추출합니다 (access 또는 refresh)
     */
    public TokenType extractTokenType(String token) {
        return OptionalUtils.tryOf(
                () -> {
                    Claims claims = extractClaims(token);
                    String type = claims.get("type", String.class);
                    return TokenType.valueOf(type.toUpperCase());
                }
            )
            .orElseThrow(() -> new InvalidTokenException("Valid token type not found in payload"));
    }

    /**
     * 토큰 Provider 추출
     */
    public String extractProvider(String token) {
        return OptionalUtils.tryOf(
                () -> {
                    Claims claims = extractClaims(token);
                    return claims.get("provider", String.class);
                }
            )
            .orElseThrow(() -> new InvalidTokenException("Valid provider not found in payload"));
    }

    /**
     * 토큰 role 추출
     */
    public RoleType extractRole(String token) {
        return OptionalUtils.tryOf(
                () -> {
                    Claims claims = extractClaims(token);
                    String role = claims.get("role", String.class);
                    return RoleType.valueOf(role.toUpperCase());
                }
            )
            .orElseThrow(() -> new InvalidTokenException("Valid role not found in payload"));
    }

<<<<<<< HEAD
=======
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
        } catch (ExpiredJwtException e) {
            // 만료된 토큰에서도 클레임은 추출 가능
            return Optional.of(e.getClaims());
        } catch (Exception e) {
            // 유효하지 않은 토큰에서는 클레임을 추출하지 않음
            return Optional.empty();
        }
    }

>>>>>>> 8947ec5 (refactor: 인증 관련 DTO, 예외 처리, JWT 필터 및 테스트 코드 리팩토링)
    public Long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }
}