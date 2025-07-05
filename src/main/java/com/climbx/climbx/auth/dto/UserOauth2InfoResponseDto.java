package com.climbx.climbx.auth.dto;

import java.time.Instant;
import lombok.Builder;

@Builder
public record UserOauth2InfoResponseDto(

    Long id,

    String nickname,

    String provider,

    Instant issuedAt, // 액세스 토큰 발급 시간

    Instant expiresAt // 액세스 토큰 만료 시간
) {

}
