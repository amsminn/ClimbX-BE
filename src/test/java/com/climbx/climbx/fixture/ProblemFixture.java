package com.climbx.climbx.fixture;

import com.climbx.climbx.problem.dto.ProblemResponseDto;
import com.climbx.climbx.problem.entity.ProblemEntity;

public class ProblemFixture {

    public static final Long DEFAULT_PROBLEM_RATING = 1200L;
    public static final String DEFAULT_GYM_NAME = "temp-gym-name";
    public static final Long DEFAULT_CLUSTER_ID = 1L;
    public static final String DEFAULT_LOCAL_LEVEL = "temp-local-level";
    public static final String DEFAULT_STANDARD_LEVEL = "temp-standard-level";

    public static ProblemEntity createProblemEntity(Long problemId) {
        return createProblemEntity(problemId, DEFAULT_PROBLEM_RATING);
    }

    public static ProblemEntity createProblemEntity(Long problemId, String problemName,
        Long problemRating) {
        return createProblemEntity(problemId, problemRating);
    }

    public static ProblemEntity createProblemEntity(Long problemId, Long problemRating) {
        return ProblemEntity.builder()
            .problemId(problemId)
            .problemRating(problemRating)
            .build();
    }

    public static ProblemResponseDto createProblemResponseDto(Long problemId) {
        return ProblemResponseDto.builder()
            .id(problemId)
            .gymName(DEFAULT_GYM_NAME)
            .clusterId(DEFAULT_CLUSTER_ID)
            .localLevel(DEFAULT_LOCAL_LEVEL)
            .standardLevel(DEFAULT_STANDARD_LEVEL)
            .build();
    }
} 