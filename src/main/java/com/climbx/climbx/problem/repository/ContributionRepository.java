package com.climbx.climbx.problem.repository;

import com.climbx.climbx.problem.entity.ContributionEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContributionRepository extends JpaRepository<ContributionEntity, Long> {

    public List<ContributionEntity> findAllByProblemEntity_ProblemId(UUID problemId);
}
