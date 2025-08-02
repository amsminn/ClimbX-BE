package com.climbx.climbx.problem.dto;

import com.climbx.climbx.problem.entity.ProblemEntity;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ProblemInfoInSpotResponseDto(

    UUID problemId,
    String localLevel,
    String holdColor,
    Integer problemRating,
    Long spotId,
    Double spotXRatio,
    Double spotYRatio,
    String problemImageCdnUrl
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
            .problemImageCdnUrl(problem.problemImageCdnUrl())
            .build();
    }
}
