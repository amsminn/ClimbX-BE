package com.climbx.climbx.common.enums;

import com.climbx.climbx.common.exception.InvalidEnumValueException;
import com.climbx.climbx.common.util.OptionalUtil;

public enum SortOrderType {

    ASC,
    DESC;

    public static String from(String value) {
        return OptionalUtil.tryOf(
            () -> SortOrderType.valueOf(value.toUpperCase()).name()
        ).orElseThrow(
            () -> new InvalidEnumValueException("SortOrderType", value)
        );
    }
}
