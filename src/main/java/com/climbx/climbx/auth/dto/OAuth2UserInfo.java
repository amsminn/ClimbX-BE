package com.climbx.climbx.auth.dto;

import lombok.Builder;

@Builder
public record OAuth2UserInfo(

    String providerId,     // 제공자별 고유 ID
    String email,
    String nickname,
    String name,
    String profileImageUrl, // 프로필 이미지 URL
    Boolean emailVerified   // 이메일 검증 여부
) {

    public String getDisplayName() {
        return nickname != null ? nickname : name;
    }
} 