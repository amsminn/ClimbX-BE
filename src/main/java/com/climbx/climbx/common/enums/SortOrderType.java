package com.climbx.climbx.common.enums;

import com.climbx.climbx.common.exception.InvalidEnumValueException;
import com.climbx.climbx.common.util.OptionalUtil;

public enum SortOrderType {

    ASC,
    DESC;

    public static SortOrderType from(String value) {
        return OptionalUtil.tryOf(
            () -> SortOrderType.valueOf(value.toUpperCase())
        ).orElseThrow(
            () -> new InvalidEnumValueException("SortOrderType", value)
        );
    }
}
