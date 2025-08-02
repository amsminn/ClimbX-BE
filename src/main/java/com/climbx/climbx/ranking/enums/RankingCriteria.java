package com.climbx.climbx.ranking.enums;

import com.climbx.climbx.common.util.OptionalUtil;
import com.climbx.climbx.ranking.exception.InvalidCriteriaException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum RankingCriteria {

    RATING("rating"),
    STREAK("currentStreak"),
    LONGEST_STREAK("longestStreak"),
    SOLVED_COUNT("solvedCount");

    private final String columnName;

    public static RankingCriteria from(String code) {
        return OptionalUtil.tryOf(() -> valueOf(code))
            .orElseThrow(() -> new InvalidCriteriaException(code));
    }
}
