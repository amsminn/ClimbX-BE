package com.climbx.climbx.common.enums;

import com.climbx.climbx.common.exception.InvalidEnumValueException;
import com.climbx.climbx.common.util.OptionalUtil;

public enum RoleType {

    USER,
    ADMIN;

    public static RoleType from(String value) {
        return OptionalUtil.tryOf(
            () -> RoleType.valueOf(value.toUpperCase())
        ).orElseThrow(
            () -> new InvalidEnumValueException("RoleType", value)
        );
    }
}
