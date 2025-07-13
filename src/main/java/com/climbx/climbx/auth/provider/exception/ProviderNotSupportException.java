package com.climbx.climbx.auth.provider.exception;

import com.climbx.climbx.auth.enums.OAuth2ProviderType;
import com.climbx.climbx.common.error.BusinessException;
import com.climbx.climbx.common.error.ErrorCode;

public class ProviderNotSupportException extends BusinessException {

    public ProviderNotSupportException(OAuth2ProviderType provider) {
        super(ErrorCode.PROVIDER_NOT_SUPPORTED);
        addContext("provider", provider.name());
    }
}
