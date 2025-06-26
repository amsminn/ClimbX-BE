package com.climbx.climbx.problem.dto;

import com.climbx.climbx.problem.entity.ProblemEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ProblemResponseDto(

    @NotNull
    @Min(1)
    Long id,

    @NotBlank
    String gymName,

    @NotNull
    @Min(1)
    Long clusterId,

    @NotBlank
    String localLevel,

    @NotBlank
    String standardLevel
) {

    public static ProblemResponseDto from(ProblemEntity problem) {
        /*
         * ProblemEntity가 아직 임시 구현 상태이므로, 추후 수정 필요.
         */
        return ProblemResponseDto.builder()
            .id(problem.problemId())
            .gymName("temp-gym-name")
            .clusterId(1L)
            .localLevel("temp-local-level")
            .standardLevel("temp-standard-level")
            .build();
    }
}
