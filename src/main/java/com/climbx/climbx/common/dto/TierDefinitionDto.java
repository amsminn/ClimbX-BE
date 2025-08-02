package com.climbx.climbx.common.dto;

import lombok.Builder;

@Builder
public record TierDefinitionDto(

    String name, // 브론즈, 실버, 골드, 플래티넘, 다이아, 마스터
    Integer level, // 1, 2, 3
    Integer minRating,
    Integer maxRating,
    Integer score
) {

}
