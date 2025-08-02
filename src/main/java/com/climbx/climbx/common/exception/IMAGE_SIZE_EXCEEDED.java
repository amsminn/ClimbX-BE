package com.climbx.climbx.common.exception;

import com.climbx.climbx.common.enums.ErrorCode;

public class IMAGE_SIZE_EXCEEDED extends BusinessException {

    public IMAGE_SIZE_EXCEEDED(ErrorCode errorCode) {
        super(errorCode);
    }

    public IMAGE_SIZE_EXCEEDED(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
