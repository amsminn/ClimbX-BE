package com.climbx.climbx.user.dto;

import com.climbx.climbx.user.entity.UserAccountEntity;
import com.climbx.climbx.user.entity.UserStatEntity;
import java.util.Map;
import lombok.Builder;

@Builder
public record UserProfileResponseDto(

    String nickname,

    String statusMessage,

    String profileImageCdnUrl, // null 허용

    Integer ranking,

    Integer rating,

    String tier,

    Map<String, Integer> categoryRatings,

    Integer currentStreak,

    Integer longestStreak,

    Integer solvedCount,

    Integer submissionCount,

    Integer contributionCount,

    Integer rivalCount
) {

    public static UserProfileResponseDto from(
        UserAccountEntity account,
        UserStatEntity stat,
        String tier,
        Integer ranking,
        Map<String, Integer> categoryRatings) {

        return UserProfileResponseDto.builder()
            .nickname(account.nickname())
            .statusMessage(account.statusMessage())
            .tier(tier)
            .profileImageCdnUrl(account.profileImageCdnUrl())
            .ranking(ranking)
            .rating(stat.rating())
            .categoryRatings(categoryRatings)
            .currentStreak(stat.currentStreak())
            .longestStreak(stat.longestStreak())
            .solvedCount(stat.solvedCount())
            .submissionCount(stat.submissionCount())
            .contributionCount(stat.contributionCount())
            .rivalCount(stat.rivalCount())
            .build();
    }
}
