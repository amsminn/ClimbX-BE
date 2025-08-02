package com.climbx.climbx.problem.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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
    Integer problemRating,

    @NotNull
    @Min(value = 1L)
    Long spotId,

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    Double spotXRatio,

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    Double spotYRatio
) {

}