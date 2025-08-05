package com.climbx.climbx.submission.dto;

import com.climbx.climbx.problem.enums.ProblemTagType;

public record TagRatingPairDto(

    ProblemTagType tag,
    Integer rating
) {

}