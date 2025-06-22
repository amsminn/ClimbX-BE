package com.climbx.climbx.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserProfileRequestDto(
    @NotBlank
    String nickname,

    @NotNull
    String statusMessage,

    String profileImageUrl // null 허용
) {
}