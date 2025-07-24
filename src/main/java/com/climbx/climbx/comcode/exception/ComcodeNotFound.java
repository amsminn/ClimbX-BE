package com.climbx.climbx.comcode.exception;

import com.climbx.climbx.common.enums.ErrorCode;
import com.climbx.climbx.common.exception.BusinessException;

public class ComcodeNotFound extends BusinessException {

    public ComcodeNotFound(String code) {
        super(ErrorCode.COMCODE_NOT_FOUND);
        addContext("wrong code ", code);
    }
}
