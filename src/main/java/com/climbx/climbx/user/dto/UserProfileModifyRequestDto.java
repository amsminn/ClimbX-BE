package com.climbx.climbx.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserProfileModifyRequestDto(
    @NotBlank
    String nickname,

    @NotNull
    String statusMessage,

    String profileImageUrl // null 허용
) {
}