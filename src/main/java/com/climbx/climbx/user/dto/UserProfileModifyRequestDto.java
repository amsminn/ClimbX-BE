package com.climbx.climbx.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserProfileModifyRequestDto(
    @NotBlank
    String newNickname,

    @NotNull
    String newStatusMessage,

    String newProfileImageUrl // null 허용
) {
}