package com.climbx.climbx.video.exception;

import com.climbx.climbx.common.error.BusinessException;
import com.climbx.climbx.common.error.ErrorCode;

public class FileExtensionNotExistsException extends BusinessException {

    public FileExtensionNotExistsException(ErrorCode errorCode) {
        super(errorCode);
    }

    public FileExtensionNotExistsException(
        ErrorCode errorCode,
        String detail
    ) {
        super(errorCode, detail);
    }
}
