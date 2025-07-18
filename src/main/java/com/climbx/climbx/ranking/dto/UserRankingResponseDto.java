package com.climbx.climbx.ranking.dto;

import com.climbx.climbx.user.entity.UserStatEntity;
import lombok.Builder;

@Builder
public record UserRankingResponseDto(

    String nickname,
    String statusMessage,
    String profileImageUrl, // null 허용
    Integer rating,
    Integer currentStreak,
    Integer longestStreak,
    Integer solvedCount
) {

    public static UserRankingResponseDto from(UserStatEntity user) {
        return UserRankingResponseDto.builder()
            .nickname(user.userAccountEntity().nickname())
            .statusMessage(user.userAccountEntity().statusMessage())
            .profileImageUrl(user.userAccountEntity().profileImageUrl())
            .rating(user.rating())
            .currentStreak(user.currentStreak())
            .longestStreak(user.longestStreak())
            .solvedCount(user.solvedProblemsCount())
            .build();
    }
}
