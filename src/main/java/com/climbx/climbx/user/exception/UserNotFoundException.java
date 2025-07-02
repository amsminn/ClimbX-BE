package com.climbx.climbx.user.exception;

import com.climbx.climbx.common.error.BusinessException;
import com.climbx.climbx.common.error.ErrorCode;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(Long userId) {
        super(ErrorCode.USER_NOT_FOUND);
        addContext("userId", userId.toString());
    }

    public UserNotFoundException(String nickname) {
        super(ErrorCode.USER_NOT_FOUND);
        addContext("nickname", nickname);
    }
}