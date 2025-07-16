package com.climbx.climbx.common.security.exception;

import com.climbx.climbx.common.error.BusinessException;
import com.climbx.climbx.common.error.ErrorCode;

public class TokenExpiredException extends BusinessException {

    public TokenExpiredException() {
        super(ErrorCode.TOKEN_EXPIRED);
    }

    public TokenExpiredException(String tokenType) {
        super(ErrorCode.TOKEN_EXPIRED);
        addContext("tokenType", tokenType);
    }
} 