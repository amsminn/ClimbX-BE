package com.climbx.climbx.problem.repository;

import com.climbx.climbx.common.enums.ActiveStatusType;
import com.climbx.climbx.problem.dto.ProblemInfoResponseDto;
import com.climbx.climbx.problem.entity.ProblemEntity;
import com.climbx.climbx.problem.enums.ProblemTier;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProblemRepository extends JpaRepository<ProblemEntity, UUID> {

    @Query("""
                SELECT new com.climbx.climbx.problem.dto.ProblemInfoResponseDto(
                    p.problemId, 
                    g.gymId,
                    g.name,
                    ga.gymAreaId,
                    ga.areaName,
                    p.localLevel, 
                    p.holdColor, 
                    p.problemRating,
                    p.problemTier, 
                    p.problemImageCdnUrl,
                    p.activeStatus,
                    p.createdAt
                )
                FROM ProblemEntity p 
                JOIN  p.gymArea ga 
                JOIN  p.gym g 
                WHERE (p.gym.gymId = :gymId OR :gymId IS NULL) AND 
                (p.gymArea.gymAreaId = :gymAreaId OR :gymAreaId IS NULL) AND 
                (p.localLevel = :localLevel OR :localLevel IS NULL) AND 
                (p.holdColor = :holdColor OR :holdColor IS NULL) AND 
                (p.problemTier = :problemTier OR :problemTier IS NULL) AND 
                (p.activeStatus = :activeStatus OR :activeStatus IS NULL)
        """)
    List<ProblemInfoResponseDto> findByGymAndAreaAndLevelAndColorAndProblemTierAndActiveStatus(
        @Param("gymId") Long gymId,
        @Param("gymAreaId") Long gymAreaId,
        @Param("localLevel") String localLevel,
        @Param("holdColor") String holdColor,
        @Param("problemTier") ProblemTier problemTier,
        @Param("activeStatus") ActiveStatusType activeStatus
    );
}
