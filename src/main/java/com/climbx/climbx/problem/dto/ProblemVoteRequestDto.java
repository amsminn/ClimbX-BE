package com.climbx.climbx.problem.dto;

import com.climbx.climbx.problem.enums.ProblemTagType;
import com.climbx.climbx.problem.enums.ProblemTierType;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ProblemVoteRequestDto(

    @NotNull
    UUID problemId,

    @NotNull
    ProblemTierType tier,

    List<ProblemTagType> tags // nullable
) {

}
