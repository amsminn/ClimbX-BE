package com.climbx.climbx.auth.provider.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfoResponseDto(
    @JsonProperty("id")
    Long id,

    @JsonProperty("connected_at")
    String connectedAt,

    @JsonProperty("kakao_account")
    KakaoAccount kakaoAccount
) {

    public record KakaoAccount(
        @JsonProperty("profile_nickname_needs_agreement")
        Boolean profileNicknameNeedsAgreement,

        @JsonProperty("has_email")
        Boolean hasEmail,

        @JsonProperty("email_needs_agreement")
        Boolean emailNeedsAgreement,

        @JsonProperty("is_email_valid")
        Boolean isEmailValid,

        @JsonProperty("is_email_verified")
        Boolean isEmailVerified,

        @JsonProperty("email")
        String email,

        @JsonProperty("profile")
        Profile profile
    ) {

    }

    public record Profile(
        @JsonProperty("nickname")
        String nickname,

        @JsonProperty("profile_image_url")
        String profileImageUrl,

        @JsonProperty("thumbnail_image_url")
        String thumbnailImageUrl,

        @JsonProperty("is_default_image")
        Boolean isDefaultImage
    ) {

    }
} 