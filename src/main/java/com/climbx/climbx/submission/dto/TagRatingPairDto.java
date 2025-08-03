package com.climbx.climbx.submission.dto;

import com.climbx.climbx.problem.enums.ProblemType;

public record TagRatingPairDto(

    ProblemType tag,
    Integer rating
) {

}