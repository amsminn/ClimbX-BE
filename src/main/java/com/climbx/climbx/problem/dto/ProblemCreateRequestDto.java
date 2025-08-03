package com.climbx.climbx.problem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ProblemCreateRequestDto(

    @NotNull
    Long gymAreaId,

    @NotNull
    @Size(min = 1, max = 32)
    String localLevel,

    @NotNull
    @Size(min = 1, max = 32)
    String holdColor,

    @NotNull
    @Min(value = 1L)
    Integer problemRating
) {

}