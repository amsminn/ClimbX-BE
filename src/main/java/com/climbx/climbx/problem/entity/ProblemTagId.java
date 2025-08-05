package com.climbx.climbx.problem.entity;

import com.climbx.climbx.problem.enums.ProblemTagType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class ProblemTagId {

    @Column(name = "problem_id")
    private Long problemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    private ProblemTagType tag;
}
