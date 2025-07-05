package com.climbx.climbx.auth.enums;

public enum OAuth2ProviderType {
    KAKAO("카카오"),
    GOOGLE("구글"),
    APPLE("애플");

    private final String displayName;

    OAuth2ProviderType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static OAuth2ProviderType fromString(String provider) {
        try {
            return valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 OAuth2 제공자입니다: " + provider);
        }
    }
} 