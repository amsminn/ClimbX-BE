package com.climbx.climbx.auth.exception;

import com.climbx.climbx.common.error.BusinessException;
import com.climbx.climbx.common.error.ErrorCode;

public class OAuth2ProviderNotSupportedException extends BusinessException {

    public OAuth2ProviderNotSupportedException(String provider) {
        super(ErrorCode.OAUTH2_PROVIDER_NOT_SUPPORTED,
            "지원하지 않는 OAuth2 제공자입니다: " + provider);
    }
} 