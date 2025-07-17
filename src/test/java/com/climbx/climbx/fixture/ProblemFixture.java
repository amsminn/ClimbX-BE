package com.climbx.climbx.fixture;

import com.climbx.climbx.gym.entity.GymEntity;
import com.climbx.climbx.problem.dto.ProblemDetailsResponseDto;
import com.climbx.climbx.problem.entity.ProblemEntity;

public class ProblemFixture {

    public static final Long DEFAULT_PROBLEM_RATING = 1200L;
    public static final String DEFAULT_LOCAL_LEVEL = "빨강";
    public static final String DEFAULT_HOLD_COLOR = "파랑";
    public static final String DEFAULT_IMAGE_URL = "http://example.com/image.jpg";
    public static final Long DEFAULT_SPOT_ID = 1L;
    public static final Double DEFAULT_SPOT_X_RATIO = 50.0;
    public static final Double DEFAULT_SPOT_Y_RATIO = 30.0;

    public static ProblemEntity createProblemEntity(Long problemId, GymEntity gym) {
        return createProblemEntity(problemId, gym, DEFAULT_LOCAL_LEVEL, DEFAULT_HOLD_COLOR,
            DEFAULT_PROBLEM_RATING, DEFAULT_SPOT_ID, DEFAULT_SPOT_X_RATIO, DEFAULT_SPOT_Y_RATIO);
    }

    public static ProblemEntity createProblemEntity(
        Long problemId,
        GymEntity gym,
        String localLevel,
        String holdColor,
        Long problemRating
    ) {
        return createProblemEntity(problemId, gym, localLevel, holdColor, problemRating,
            DEFAULT_SPOT_ID, DEFAULT_SPOT_X_RATIO, DEFAULT_SPOT_Y_RATIO);
    }

    public static ProblemEntity createProblemEntity(
        Long problemId,
        GymEntity gym,
        String localLevel,
        String holdColor
    ) {
        return createProblemEntity(problemId, gym, localLevel, holdColor, DEFAULT_PROBLEM_RATING,
            DEFAULT_SPOT_ID, DEFAULT_SPOT_X_RATIO, DEFAULT_SPOT_Y_RATIO);
    }

    public static ProblemEntity createProblemEntity(
        Long problemId,
        GymEntity gym,
        String localLevel,
        String holdColor,
        Long problemRating,
        Long spotId,
        Double spotXRatio,
        Double spotYRatio
    ) {
        return ProblemEntity.builder()
            .problemId(problemId)
            .gym(gym)
            .localLevel(localLevel)
            .holdColor(holdColor)
            .problemRating(problemRating)
            .spotId(spotId)
            .spotXRatio(spotXRatio)
            .spotYRatio(spotYRatio)
            .imageUrl(DEFAULT_IMAGE_URL)
            .build();
    }

    public static ProblemDetailsResponseDto createProblemResponseDto(
        Long problemId,
        Long gymId,
        String gymName
    ) {
        return createProblemResponseDto(problemId, gymId, gymName, DEFAULT_LOCAL_LEVEL,
            DEFAULT_HOLD_COLOR, DEFAULT_PROBLEM_RATING, DEFAULT_SPOT_ID, DEFAULT_SPOT_X_RATIO,
            DEFAULT_SPOT_Y_RATIO);
    }

    public static ProblemDetailsResponseDto createProblemResponseDto(
        Long problemId,
        Long gymId,
        String gymName,
        String localLevel,
        String holdColor,
        Long problemRating
    ) {
        return createProblemResponseDto(problemId, gymId, gymName, localLevel, holdColor,
            problemRating, DEFAULT_SPOT_ID, DEFAULT_SPOT_X_RATIO, DEFAULT_SPOT_Y_RATIO);
    }

    public static ProblemDetailsResponseDto createProblemResponseDto(
        Long problemId,
        Long gymId,
        String gymName,
        String localLevel,
        String holdColor,
        Long problemRating,
        Long spotId,
        Double spotXRatio,
        Double spotYRatio
    ) {
        return ProblemDetailsResponseDto.builder()
            .problemId(problemId)
            .gymId(gymId)
            .gymName(gymName)
            .localLevel(localLevel)
            .holdColor(holdColor)
            .problemRating(problemRating)
            .spotId(spotId)
            .spotXRatio(spotXRatio)
            .spotYRatio(spotYRatio)
            .imageUrl(DEFAULT_IMAGE_URL)
            .build();
    }
} 