package com.climbx.climbx.ranking.exception;

import com.climbx.climbx.common.enums.ErrorCode;
import com.climbx.climbx.common.exception.BusinessException;

public class InvalidCriteriaException extends BusinessException {

    public InvalidCriteriaException(String criteria) {
        super(ErrorCode.INVALID_RANKING_CRITERIA);
        addContext("criteria", criteria);
    }
}
