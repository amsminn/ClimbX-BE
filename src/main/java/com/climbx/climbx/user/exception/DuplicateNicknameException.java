package com.climbx.climbx.user.exception;

import com.climbx.climbx.common.error.BusinessException;
import com.climbx.climbx.common.error.ErrorCode;

public class DuplicateNicknameException extends BusinessException {

    public DuplicateNicknameException(String nickname) {
        super(ErrorCode.DUPLICATED_NICKNAME);
        addContext("nickname", nickname);
    }
}