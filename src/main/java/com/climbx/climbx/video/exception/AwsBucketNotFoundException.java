package com.climbx.climbx.video.exception;

import com.climbx.climbx.common.error.BusinessException;
import com.climbx.climbx.common.error.ErrorCode;

public class AwsBucketNotFoundException extends BusinessException {

    public AwsBucketNotFoundException(ErrorCode errorCode) {
        super(errorCode);
        addContext("errorCode", errorCode.name());
    }

    public AwsBucketNotFoundException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
        addContext("errorCode", errorCode.name());
    }
}
