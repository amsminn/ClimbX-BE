package com.climbx.climbx.problem.exception;

import com.climbx.climbx.common.enums.ErrorCode;
import com.climbx.climbx.common.exception.BusinessException;

public class ProblemNotFoundException extends BusinessException {

    public ProblemNotFoundException(Long problemId) {
        super(ErrorCode.PROBLEM_NOT_FOUND);
        addContext("problemId", problemId.toString());
    }
}
