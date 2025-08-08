package com.climbx.climbx.common.dto;

import com.climbx.climbx.problem.enums.ProblemTierType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record VoteTierDto(

    ProblemTierType tier,
    LocalDateTime dateTime
) {


}
