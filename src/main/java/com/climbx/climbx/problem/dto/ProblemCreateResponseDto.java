package com.climbx.climbx.problem.dto;

import com.climbx.climbx.common.enums.ActiveStatusType;
import com.climbx.climbx.problem.entity.ProblemEntity;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ProblemCreateResponseDto(

    UUID problemId,
    Long gymId,
    String gymName,
    Long gymAreaId,
    String areaName,
    String localLevel,
    String holdColor,
    Integer problemRating,
    Long spotId,
    Double spotXRatio,
    Double spotYRatio,
    String problemImageCdnUrl,
    ActiveStatusType status
) {

    public static ProblemCreateResponseDto from(ProblemEntity problem) {
        return ProblemCreateResponseDto.builder()
            .problemId(problem.problemId())
            .gymId(problem.gym().gymId())
            .gymName(problem.gym().name())
            .gymAreaId(problem.gymArea().gymAreaId())
            .areaName(problem.gymArea().areaName())
            .localLevel(problem.localLevel())
            .holdColor(problem.holdColor())
            .problemRating(problem.problemRating())
            .spotId(problem.spotId())
            .spotXRatio(problem.spotXRatio())
            .spotYRatio(problem.spotYRatio())
            .problemImageCdnUrl(problem.problemImageCdnUrl())
            .status(problem.status())
            .build();
    }
}