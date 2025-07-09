package com.climbx.climbx.common.security.exception;

import com.climbx.climbx.common.error.BusinessException;
import com.climbx.climbx.common.error.ErrorCode;

public class InvalidTokenException extends BusinessException {

    public InvalidTokenException(String message) {
        super(ErrorCode.INVALID_TOKEN, message);
    }

    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }
} 