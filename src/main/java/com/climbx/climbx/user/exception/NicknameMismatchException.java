package com.climbx.climbx.user.exception;

import com.climbx.climbx.common.error.BusinessException;
import com.climbx.climbx.common.error.ErrorCode;

public class NicknameMismatchException extends BusinessException {

    public NicknameMismatchException(String pathNickname, String bodyNickname) {
        super(ErrorCode.NICKNAME_MISMATCH);
        addContext("pathNickname", pathNickname);
        addContext("bodyNickname", bodyNickname);
    }
}