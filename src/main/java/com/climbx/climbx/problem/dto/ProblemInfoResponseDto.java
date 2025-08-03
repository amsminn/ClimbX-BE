package com.climbx.climbx.problem.dto;

import com.climbx.climbx.common.enums.ActiveStatusType;
import com.climbx.climbx.problem.entity.GymAreaEntity;
import com.climbx.climbx.problem.entity.ProblemEntity;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ProblemInfoResponseDto(

    UUID problemId,
    Long gymId,
    Long gymAreaId,
    String gymAreaName,
    String localLevel,
    String holdColor,
    Integer problemRating,
    String problemImageCdnUrl,
    ActiveStatusType activeStatus,
    LocalDateTime createdAt
) {

    public static ProblemInfoResponseDto from(
        ProblemEntity problem,
        Long gymId,
        GymAreaEntity gymArea
    ) {
        return ProblemInfoResponseDto.builder()
            .problemId(problem.problemId())
            .gymId(gymId)
            .gymAreaId(gymArea.gymAreaId())
            .gymAreaName(gymArea.areaName())
            .localLevel(problem.localLevel())
            .holdColor(problem.holdColor())
            .problemRating(problem.problemRating())
            .problemImageCdnUrl(problem.problemImageCdnUrl())
            .activeStatus(problem.activeStatus())
            .createdAt(problem.createdAt())
            .build();
    }
}
