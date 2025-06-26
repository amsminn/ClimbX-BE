package com.climbx.climbx.user.dto;

import com.climbx.climbx.user.entity.UserAccountEntity;
import com.climbx.climbx.user.entity.UserStatEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record UserProfileResponseDto(

    @NonNull
    @NotBlank
    String nickname,

    @NonNull
    @NotNull
    String statusMessage,

    String profileImageUrl, // null 허용

    @NonNull
    @NotNull @Min(1)
    Long ranking,

    @NotNull @Min(0)
    Long rating,

    @NotNull
    Map<@NotBlank String, @NotNull @Min(0) Long> categoryRatings,

    @NotNull @Min(0)
    Long currentStreak,

    @NotNull @Min(0)
    Long longestStreak,

    @NotNull @Min(0)
    Long solvedProblemsCount,

    @NotNull @Min(0)
    Long rivalCount
) {

    public static UserProfileResponseDto from(
        UserAccountEntity account,
        UserStatEntity stat,
        Long ranking,
        Map<String, Long> categoryRatings) {

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
