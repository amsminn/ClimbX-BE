package com.climbx.climbx.auth.exception;

import com.climbx.climbx.auth.enums.OAuth2ProviderType;
import com.climbx.climbx.common.error.BusinessException;
import com.climbx.climbx.common.error.ErrorCode;

public class ProviderUserInfoFetchFailedException extends BusinessException {

    public ProviderUserInfoFetchFailedException(OAuth2ProviderType provider) {
        super(ErrorCode.PRODIVDER_USER_INFO_FETCH_FAILED);
        addContext("provider", provider.name());
    }
} 