package com.climbx.climbx.auth.enums;

import lombok.Getter;

@Getter
public enum OAuth2ProviderType {
    KAKAO,
    GOOGLE,
    APPLE;

    public static OAuth2ProviderType fromString(String provider) {
        try {
            return valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 OAuth2 제공자입니다: " + provider);
        }
    }
}