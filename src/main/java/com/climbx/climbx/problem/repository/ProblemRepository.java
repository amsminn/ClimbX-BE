package com.climbx.climbx.problem.repository;

import com.climbx.climbx.common.enums.ActiveStatusType;
import com.climbx.climbx.problem.entity.ProblemEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProblemRepository extends JpaRepository<ProblemEntity, UUID> {

    @Query("SELECT p "
        + "FROM ProblemEntity p "
        + "WHERE p.gym.gymId = :gymId AND "
        + "p.gymArea.gymAreaId = :gymAreaId AND "
        + "p.localLevel = :localLevel AND "
        + "p.holdColor = :holdColor AND "
        + "p.activeStatus = :activeStatus")
    List<ProblemEntity> findByGymAndAreaAndLevelAndColorAndActiveStatus(
        @Param("gymId") Long gymId,
        @Param("gymAreaId") Long gymAreaId,
        @Param("localLevel") String localLevel,
        @Param("holdColor") String holdColor,
        @Param("activeStatus") ActiveStatusType activeStatus
    );
}
