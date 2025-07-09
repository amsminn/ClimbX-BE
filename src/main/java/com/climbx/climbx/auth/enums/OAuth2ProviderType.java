package com.climbx.climbx.auth.enums;

public enum OAuth2ProviderType {
    KAKAO("카카오"),
    GOOGLE("구글"),
    APPLE("애플");

    private final String provider;

    OAuth2ProviderType(String displayName) {
        this.provider = displayName;
    }

    public static OAuth2ProviderType fromString(String provider) {
        try {
            return valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 OAuth2 제공자입니다: " + provider);
        }
    }

    public String getProvider() {
        return provider;
    }
} 