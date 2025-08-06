package com.climbx.climbx.problem.repository;

import com.climbx.climbx.problem.entity.ContributionTagEnitty;
import com.climbx.climbx.problem.entity.ProblemTagId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContributionTagRepository extends
    JpaRepository<ContributionTagEnitty, ProblemTagId> {

}
