package com.climbx.climbx.problem.repository;

import com.climbx.climbx.problem.entity.ContributionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContributionRepository extends JpaRepository<ContributionEntity, Long> {
    
}
