package com.climbx.climbx.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RefreshRequestDto(
    @NotBlank
    String refreshToken
) {
}
