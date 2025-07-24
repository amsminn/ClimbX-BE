package com.climbx.climbx.common.enums;

import com.climbx.climbx.common.exception.InvalidEnumValueException;
import com.climbx.climbx.common.util.OptionalUtil;

public enum RoleType {

    USER,
    ADMIN;

    public static String from(String value) {
        return OptionalUtil.tryOf(
            () -> RoleType.valueOf(value.toUpperCase()).name()
        ).orElseThrow(
            () -> new InvalidEnumValueException("RoleType", value)
        );
    }
}
