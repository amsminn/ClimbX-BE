package com.climbx.climbx.ranking.enums;

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
    LONGESTSTREAK("longestStreak"),
    SOLVEDPROBLEMSCOUNT("solvedProblemsCount");

    private final String columnName;

    public static String fromCode(String code) {
        try {
            return RankingCriteria.valueOf(code.toUpperCase()).columnName();
        } catch (IllegalArgumentException e) {
            throw new InvalidCriteriaException(code);
        }
    }
}
