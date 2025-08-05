package com.climbx.climbx.problem.dto;

import com.climbx.climbx.common.enums.ActiveStatusType;
import com.climbx.climbx.problem.entity.GymAreaEntity;
import com.climbx.climbx.problem.entity.ProblemEntity;
import com.climbx.climbx.problem.enums.ProblemTierType;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ProblemDetailsResponseDto(

    UUID problemId,
    Long gymId,
    String gymName,
    Long gymAreaId,
    String gymAreaName,
    String localLevel,
    String holdColor,
    Integer problemRating,
    ProblemTierType problemTier,
    String problemImageCdnUrl,
    ActiveStatusType activeStatus
) {

    public static ProblemDetailsResponseDto from(ProblemEntity problem, GymAreaEntity gymArea) {
        return ProblemDetailsResponseDto.builder()
            .problemId(problem.problemId())
            .gymId(problem.gym().gymId())
            .gymName(problem.gym().name())
            .gymAreaId(gymArea.gymAreaId())
            .gymAreaName(gymArea.areaName())
            .localLevel(problem.localLevel())
            .holdColor(problem.holdColor())
            .problemRating(problem.problemRating())
            .problemTier(problem.problemTier())
            .problemImageCdnUrl(problem.problemImageCdnUrl())
            .activeStatus(problem.activeStatus())
            .build();
    }
}
