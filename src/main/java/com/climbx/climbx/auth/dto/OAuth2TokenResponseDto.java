package com.climbx.climbx.auth.dto;

import lombok.Builder;

@Builder
public record OAuth2TokenResponseDto(
    String accessToken,
    String refreshToken,
    String tokenType,
    Long expiresIn,
    String scope,
    String idToken // OpenID Connect용 (Apple, Google)
) {

}