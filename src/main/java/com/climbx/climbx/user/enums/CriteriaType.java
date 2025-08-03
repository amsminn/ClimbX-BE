package com.climbx.climbx.user.enums;

import com.climbx.climbx.common.exception.InvalidEnumValueException;
import com.climbx.climbx.common.util.OptionalUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CriteriaType {

    RATING,
    TOTAL_STREAK,
    LONGEST_STREAK,
    SOLVED_COUNT;

    public static CriteriaType from(String value) {
        return OptionalUtil.tryOf(() -> valueOf(value))
            .orElseThrow(() -> new InvalidEnumValueException("CriteriaType", value));
    }

    public String getLowerCaseName() {
        return this.name().toLowerCase();
    }
}
