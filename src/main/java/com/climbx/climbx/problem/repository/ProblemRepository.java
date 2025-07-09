package com.climbx.climbx.problem.repository;

import com.climbx.climbx.problem.entity.ProblemEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<ProblemEntity, Long> {

    List<ProblemEntity> findByGym_GymIdAndLocalLevelAndHoldColor(
        Long gymId, String localLevel, String holdColor
    );
}
