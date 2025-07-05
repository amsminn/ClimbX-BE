package com.climbx.climbx.auth.dto;

import lombok.Builder;

@Builder
public record OAuth2UserInfo(
    String providerId,     // 제공자별 고유 ID
    String email,
    String nickname,
    String profileImageUrl,
    String name,           // 실명 (Apple에서 제공)
    boolean emailVerified
) {
    
    public String getDisplayName() {
        return nickname != null ? nickname : name;
    }
} 