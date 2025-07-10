package com.climbx.climbx.problem.dto;

import com.climbx.climbx.problem.entity.ProblemEntity;
import lombok.Builder;

@Builder
public record ProblemDetailsResponseDto(

    Long problemId,
    Long gymId,
    String gymName,
    String localLevel,
    String holdColor,
    Long problemRating,
    Long spotId,
    Double spotXRatio,
    Double spotYRatio,
    String imageUrl
) {

    public static ProblemDetailsResponseDto from(ProblemEntity problem) {
        return ProblemDetailsResponseDto.builder()
            .problemId(problem.problemId())
            .gymId(problem.gym().gymId())
            .gymName(problem.gym().name())
            .localLevel(problem.localLevel())
            .holdColor(problem.holdColor())
            .problemRating(problem.problemRating())
            .spotId(problem.spotId())
            .spotXRatio(problem.spotXRatio())
            .spotYRatio(problem.spotYRatio())
            .imageUrl(problem.imageUrl())
            .build();
    }
}
