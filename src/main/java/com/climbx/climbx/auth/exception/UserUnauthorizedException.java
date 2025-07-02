package com.climbx.climbx.auth.exception;

import com.climbx.climbx.common.error.BusinessException;
import com.climbx.climbx.common.error.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserUnauthorizedException extends BusinessException {

    String detail;

    public UserUnauthorizedException(String detail) {
        super(ErrorCode.UNAUTHORIZED);
        this.detail = detail;
    }
}
