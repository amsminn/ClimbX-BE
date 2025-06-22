package com.climbx.climbx.auth.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginResponseDto(
    @NotBlank
    String tokenType, // e.g., "Bearer"

    @NotBlank
    String accessToken, // JWT token

    @Nullable
    String refreshToken, // 임시 구현이기 떄문에 null

    @NotNull @Min(0)
    long expiresIn // 토큰 만료 시간 (초 단위, 임시로 3600초)
) {
}
