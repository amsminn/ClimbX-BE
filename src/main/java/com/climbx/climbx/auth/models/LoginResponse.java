package com.climbx.climbx.auth.models;

import jakarta.validation.constraints.NotBlank;

public record LoginResponse(
    @NotBlank String tokenType, // e.g., "Bearer"

    @NotBlank String accessToken, // JWT token

    String refreshToken, // 임시 구현이기 떄문에 null

    @NotBlank long expiresIn // 토큰 만료 시간 (초 단위, 임시로 3600초)
){}
