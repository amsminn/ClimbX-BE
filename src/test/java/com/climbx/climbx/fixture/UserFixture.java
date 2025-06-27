package com.climbx.climbx.fixture;

import com.climbx.climbx.user.dto.UserProfileResponseDto;
import com.climbx.climbx.user.entity.UserAccountEntity;
import com.climbx.climbx.user.entity.UserStatEntity;
import java.util.Collections;

public class UserFixture {

    public static final String DEFAULT_NICKNAME = "testUser";
    public static final String DEFAULT_STATUS_MESSAGE = "Test status message";
    public static final String DEFAULT_PROFILE_IMAGE_URL = "http://example.com/profile.jpg";
    public static final Long DEFAULT_RATING = 1500L;
    public static final Long DEFAULT_CURRENT_STREAK = 5L;
    public static final Long DEFAULT_LONGEST_STREAK = 15L;
    public static final Long DEFAULT_SOLVED_PROBLEMS_COUNT = 25L;
    public static final Long DEFAULT_RIVAL_COUNT = 3L;
    public static final Long DEFAULT_RANKING = 10L;

    // UserAccountEntity 생성 메서드들
    public static UserAccountEntity from(Long userId) {
        return userAccountEntityFrom(userId, DEFAULT_NICKNAME);
    }

    public static UserAccountEntity userAccountEntityFrom(Long userId, String nickname) {
        return userAccountEntityFrom(userId, nickname, DEFAULT_STATUS_MESSAGE, DEFAULT_PROFILE_IMAGE_URL);
    }

    public static UserAccountEntity userAccountEntityFrom(Long userId, String nickname, 
        String statusMessage, String profileImageUrl) {
        return UserAccountEntity.builder()
            .userId(userId)
            .nickname(nickname)
            .statusMessage(statusMessage)
            .profileImageUrl(profileImageUrl)
            .build();
    }

    // UserStatEntity 생성 메서드들  
    public static UserStatEntity userStatEntityFrom(Long userId) {
        return userStatEntityFrom(userId, DEFAULT_RATING);
    }

    public static UserStatEntity userStatEntityFrom(Long userId, Long rating) {
        return userStatEntityFrom(userId, rating, DEFAULT_CURRENT_STREAK, 
            DEFAULT_LONGEST_STREAK, DEFAULT_SOLVED_PROBLEMS_COUNT, DEFAULT_RIVAL_COUNT);
    }

    public static UserStatEntity userStatEntityFrom(Long userId, Long rating, 
        Long currentStreak, Long longestStreak, Long solvedProblemsCount, Long rivalCount) {
        return UserStatEntity.builder()
            .userId(userId)
            .rating(rating)
            .currentStreak(currentStreak)
            .longestStreak(longestStreak)
            .solvedProblemsCount(solvedProblemsCount)
            .rivalCount(rivalCount)
            .build();
    }

    // UserProfileResponseDto 생성 메서드
    public static UserProfileResponseDto userProfileResponseDtoFrom(String nickname, Long ranking) {
        return userProfileResponseDtoFrom(nickname, ranking, DEFAULT_RATING);
    }

    public static UserProfileResponseDto userProfileResponseDtoFrom(String nickname, Long ranking, Long rating) {
        return UserProfileResponseDto.builder()
            .nickname(nickname)
            .statusMessage(DEFAULT_STATUS_MESSAGE)
            .profileImageUrl(DEFAULT_PROFILE_IMAGE_URL)
            .ranking(ranking)
            .rating(rating)
            .categoryRatings(Collections.emptyMap())
            .currentStreak(DEFAULT_CURRENT_STREAK)
            .longestStreak(DEFAULT_LONGEST_STREAK)
            .solvedProblemsCount(DEFAULT_SOLVED_PROBLEMS_COUNT)
            .rivalCount(DEFAULT_RIVAL_COUNT)
            .build();
    }

    public static UserProfileResponseDto userProfileResponseDtoFrom(String nickname, String statusMessage, 
        String profileImageUrl, Long ranking, Long rating, Long currentStreak, Long longestStreak, 
        Long solvedProblemsCount, Long rivalCount) {
        return UserProfileResponseDto.builder()
            .nickname(nickname)
            .statusMessage(statusMessage)
            .profileImageUrl(profileImageUrl)
            .ranking(ranking)
            .rating(rating)
            .categoryRatings(Collections.emptyMap())
            .currentStreak(currentStreak)
            .longestStreak(longestStreak)
            .solvedProblemsCount(solvedProblemsCount)
            .rivalCount(rivalCount)
            .build();
    }
} 