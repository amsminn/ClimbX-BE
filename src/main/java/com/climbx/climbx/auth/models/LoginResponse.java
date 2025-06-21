package com.climbx.climbx.auth.models;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record LoginResponse(
    @NotBlank
    String tokenType, // e.g., "Bearer"

    @NotBlank
    String accessToken, // JWT token

    @Nullable
    String refreshToken, // 임시 구현이기 떄문에 null

    @Positive
    long expiresIn // 토큰 만료 시간 (초 단위, 임시로 3600초)
) {
}
