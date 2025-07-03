package com.climbx.climbx.user.exception;

import com.climbx.climbx.common.error.BusinessException;
import com.climbx.climbx.common.error.ErrorCode;

public class UserStatNotFoundException extends BusinessException {

    public UserStatNotFoundException(Long userId) {
        super(ErrorCode.USER_STAT_NOT_FOUND);
        addContext("userId", userId.toString());
    }
}