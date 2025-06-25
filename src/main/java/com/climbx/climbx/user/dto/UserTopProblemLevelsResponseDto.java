package com.climbx.climbx.user.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record UserTopProblemLevelsResponseDto(
    @NotNull
    @Min(0)
    Long problemCount,

    @NotNull
    List<Long> problemLevels
) {
}
