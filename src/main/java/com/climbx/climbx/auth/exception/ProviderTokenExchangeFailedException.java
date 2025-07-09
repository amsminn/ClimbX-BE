package com.climbx.climbx.auth.exception;

import com.climbx.climbx.auth.enums.OAuth2ProviderType;
import com.climbx.climbx.common.error.BusinessException;
import com.climbx.climbx.common.error.ErrorCode;

public class ProviderTokenExchangeFailedException extends BusinessException {

    public ProviderTokenExchangeFailedException(OAuth2ProviderType provider) {
        super(ErrorCode.PROVIDER_TOKEN_EXCHANGE_FAILED);
        addContext("provider", provider.name());
    }
} 