package com.climbx.climbx.auth.dto;

import lombok.Builder;

@Builder
public record TokenGenerationResponseDto(

    AccessTokenResponseDto accessToken,
    String refreshToken
) {

}
