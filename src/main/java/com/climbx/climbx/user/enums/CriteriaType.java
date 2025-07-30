package com.climbx.climbx.user.enums;

import com.climbx.climbx.common.exception.InvalidEnumValueException;
import com.climbx.climbx.common.util.OptionalUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum CriteriaType {

    RATING("rating"),
    STREAK("current_streak"),
    LONGGEST_STREAK("longest_streak"),
    SOLVED_COUNT("solved_problems_count");

    private final String sortKey;

    public Sort.Direction getDirection() {
        return Sort.Direction.DESC;
    }

    public static CriteriaType from(String value) {
        return OptionalUtil.tryOf(() -> valueOf(value))
            .orElseThrow(() -> new InvalidEnumValueException("CriteriaType", value));
    }
}
