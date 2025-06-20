package com.climbx.climbx.auth.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record UserSSOInfoResponse(
    @NotNull String  id,

    @NotBlank String nickname,

    @NotBlank String  provider,

    @NotNull Instant issuedAt, // 액세스 토큰 발급 시간

    @NotNull Instant expiresAt // 액세스 토큰 만료 시간
) {}
