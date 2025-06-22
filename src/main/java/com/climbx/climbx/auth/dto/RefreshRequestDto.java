package com.climbx.climbx.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequestDto(
    @NotBlank
    String refreshToken
) {
}
