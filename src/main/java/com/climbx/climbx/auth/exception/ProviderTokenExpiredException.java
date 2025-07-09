package com.climbx.climbx.auth.exception;

import com.climbx.climbx.auth.enums.OAuth2ProviderType;
import com.climbx.climbx.common.error.BusinessException;
import com.climbx.climbx.common.error.ErrorCode;

public class ProviderTokenExpiredException extends BusinessException {

    public ProviderTokenExpiredException(OAuth2ProviderType providerType) {
        super(ErrorCode.PROVIDER_TOKEN_EXPIRED);
        addContext("provider", providerType.name());
    }
} 