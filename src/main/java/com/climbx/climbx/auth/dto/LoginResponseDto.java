package com.climbx.climbx.auth.dto;

import lombok.Builder;

@Builder
public record LoginResponseDto(

    String tokenType, // e.g., "Bearer"
    String accessToken, // JWT token
    String refreshToken, // 임시 구현이기 떄문에 null
    long expiresIn // 토큰 만료 시간 (초 단위, 임시로 3600초)
) {

}
