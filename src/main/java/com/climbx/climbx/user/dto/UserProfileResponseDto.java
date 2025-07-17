package com.climbx.climbx.user.dto;

import com.climbx.climbx.user.entity.UserAccountEntity;
import com.climbx.climbx.user.entity.UserStatEntity;
import java.util.Map;
import lombok.Builder;

@Builder
public record UserProfileResponseDto(

    String nickname,

    String statusMessage,

    String profileImageUrl, // null 허용

    Integer ranking,

    Integer rating,

    Map<String, Integer> categoryRatings,

    Integer currentStreak,

    Integer longestStreak,

    Integer solvedProblemsCount,

    Integer rivalCount
) {

    public static UserProfileResponseDto from(
        UserAccountEntity account,
        UserStatEntity stat,
        Integer ranking,
        Map<String, Integer> categoryRatings) {

        return UserProfileResponseDto.builder()
            .nickname(account.nickname())
            .statusMessage(account.statusMessage())
            .profileImageUrl(account.profileImageUrl())
            .ranking(ranking)
            .rating(stat.rating())
            .categoryRatings(categoryRatings)
            .currentStreak(stat.currentStreak())
            .longestStreak(stat.longestStreak())
            .solvedProblemsCount(stat.solvedProblemsCount())
            .rivalCount(stat.rivalCount())
            .build();
    }
}
