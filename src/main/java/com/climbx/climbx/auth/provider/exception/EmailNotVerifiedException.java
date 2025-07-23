package com.climbx.climbx.auth.provider.exception;

import com.climbx.climbx.auth.enums.OAuth2ProviderType;
import com.climbx.climbx.common.error.BusinessException;
import com.climbx.climbx.common.error.ErrorCode;

public class EmailNotVerifiedException extends BusinessException {

    public EmailNotVerifiedException(OAuth2ProviderType provider) {
        super(ErrorCode.EMAIL_NOT_VERIFIED);
        addContext("provider", provider.name());
    }
}
