package com.climbx.climbx.problem.entity;

import com.climbx.climbx.problem.enums.ProblemType;
import jakarta.persistence.Column;

public class ProblemTagId {

    @Column(name = "problem_id")
    private Long problemId;

    @Column(name = "tag")
    private ProblemType tag;
}
