package com.climbx.climbx.gym.exception;

import com.climbx.climbx.common.error.BusinessException;
import com.climbx.climbx.common.error.ErrorCode;

public class GymNotFoundException extends BusinessException {

    public GymNotFoundException(Long gymId) {
        super(ErrorCode.GYM_NOT_FOUND);
        addContext("gymId", gymId.toString());
    }
} 