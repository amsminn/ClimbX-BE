package com.climbx.climbx.ranking.exception;

import com.climbx.climbx.common.error.BusinessException;
import com.climbx.climbx.common.error.ErrorCode;

public class InvalidCriteriaException extends BusinessException {

    public InvalidCriteriaException(String criteria) {
        super(ErrorCode.INVALID_RANKING_CRITERIA);
        addContext("criteria", criteria);
    }
}
