package com.climbx.climbx.auth.dto;

import lombok.Builder;

@Builder
public record CallbackResponseDto(
    
    String tokenType, // e.g., "Bearer"
    String accessToken, // JWT token
    long expiresIn, // 토큰 만료 시간 (초 단위)
    String refreshToken // 내부 처리용 (HTTP cookie 설정에 사용)
) {

}
