package com.climbx.climbx.common.security.dto;

import lombok.Builder;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * JWT 토큰 파싱 결과를 담는 DTO
 */
@Builder
public record JwtTokenInfo(

    Long userId,
    String issuer,
    String audience,
    String role,
    String tokenType
) {

    public static JwtTokenInfo from(Jwt jwt) {
        return JwtTokenInfo.builder()
            .userId(Long.parseLong(jwt.getSubject()))
            .issuer(jwt.getIssuer().toString())
            .audience(jwt.getAudience().stream().findFirst().orElse(null))
            .role(jwt.getClaimAsString("role"))
            .tokenType(jwt.getClaimAsString("type"))
            .build();
    }
} 