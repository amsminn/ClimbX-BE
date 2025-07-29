package com.climbx.climbx.auth.enums;

import com.climbx.climbx.auth.provider.exception.ProviderNotSupportedException;
import com.climbx.climbx.common.util.OptionalUtil;
import lombok.Getter;

@Getter
public enum OAuth2ProviderType {
    KAKAO,
    GOOGLE,
    APPLE;

    public static OAuth2ProviderType fromString(String provider) {
        return OptionalUtil.tryOf(() -> valueOf(provider))
            .orElseThrow(() -> new ProviderNotSupportedException(provider));
    }
}