package com.climbx.climbx.auth.exception;

import com.climbx.climbx.common.error.BusinessException;
import com.climbx.climbx.common.error.ErrorCode;
 
public class OAuth2TokenExchangeFailedException extends BusinessException {
    public OAuth2TokenExchangeFailedException(String message) {
        super(ErrorCode.OAUTH2_TOKEN_EXCHANGE_FAILED, message);
    }
} 