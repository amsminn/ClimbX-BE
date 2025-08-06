package com.climbx.climbx.problem.repository;

import com.climbx.climbx.problem.entity.ProblemTagEntity;
import com.climbx.climbx.problem.entity.ProblemTagId;
import com.climbx.climbx.problem.enums.ProblemTagType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemTagRepository extends JpaRepository<ProblemTagEntity, ProblemTagId> {

    Optional<ProblemTagEntity> findByProblemIdAndTag(UUID problemId, ProblemTagType tag);
}
