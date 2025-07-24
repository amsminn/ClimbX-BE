package com.climbx.climbx.user.enums;

import com.climbx.climbx.common.exception.InvalidEnumValueException;
import com.climbx.climbx.common.util.OptionalUtil;

public enum CriteriaType {

    RATING,
    STREAK,
    LONGGEST_STREAK,
    SOLVED_COUNT;

    public static CriteriaType from(String value) {
        return OptionalUtil.tryOf(
            () -> CriteriaType.valueOf(value.toUpperCase())
        ).orElseThrow(
            () -> new InvalidEnumValueException("CriteriaType", value)
        );
    }
}
