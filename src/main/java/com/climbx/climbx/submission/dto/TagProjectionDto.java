package com.climbx.climbx.submission.dto;

import com.climbx.climbx.problem.enums.ProblemType;

public interface TagProjectionDto {

    ProblemType getTag();

    Long getRating();
}
