package com.climbx.climbx.common.security.exception;

import com.climbx.climbx.common.error.BusinessException;
import com.climbx.climbx.common.error.ErrorCode;

public class TokenExpiredException extends BusinessException {

    public TokenExpiredException(String message) {
        super(ErrorCode.TOKEN_EXPIRED, message);
    }

    public TokenExpiredException() {
        super(ErrorCode.TOKEN_EXPIRED);
    }
} 