package com.climbx.climbx.problem.dto;

import com.climbx.climbx.problem.entity.ProblemEntity;
import lombok.Builder;

@Builder
public record ProblemInfoInSpotResponseDto(

    Long problemId,
    String localLevel,
    String holdColor,
    Integer problemRating,
    Long spotId,
    Double spotXRatio,
    Double spotYRatio,
    String imageUrl
) {

    public static ProblemInfoInSpotResponseDto from(ProblemEntity problem) {
        return ProblemInfoInSpotResponseDto.builder()
            .problemId(problem.problemId())
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
