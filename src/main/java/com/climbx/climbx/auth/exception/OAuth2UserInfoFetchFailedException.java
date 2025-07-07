package com.climbx.climbx.auth.exception;

import com.climbx.climbx.common.error.BusinessException;
import com.climbx.climbx.common.error.ErrorCode;

public class OAuth2UserInfoFetchFailedException extends BusinessException {

    public OAuth2UserInfoFetchFailedException(String message) {
        super(ErrorCode.OAUTH2_USER_INFO_FETCH_FAILED, message);
    }
} 