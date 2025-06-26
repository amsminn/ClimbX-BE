package com.climbx.climbx.submission.repository;

import com.climbx.climbx.common.enums.StatusType;
import com.climbx.climbx.problem.entity.ProblemEntity;
import com.climbx.climbx.submission.entity.SubmissionEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubmissionRepository extends JpaRepository<SubmissionEntity, Long> {


    /**
     * 사용자가 푼(accepted=true) Submission 에서
     * video.userAccount.userId = :userId 인 것들만 추리고,
     * 그 안의 Problem(p) 을 DISTINCT 하여
     * p.rating DESC 순으로 정렬한 뒤
     * Pageable 로 페이지(=상위 N개) 리미트
     */
    @Query("""
        SELECT DISTINCT s.problemEntity
          FROM SubmissionEntity s
          JOIN s.videoEntity v
         WHERE v.userId = :userId
           AND s.status = com.climbx.climbx.common.enums.StatusType.ACCEPTED
         ORDER BY s.problemEntity.problemRating DESC
        """)
    List<ProblemEntity> getUserSubmissionProblems(
        @Param("userId") Long userId,
        Pageable pageable
    );

    /**
     * 사용자가 특정 기간 동안 일별로 푼 문제 수를 조회
     * from, to가 null이면 모든 기간
     */
    @Query("""
        SELECT DATE(s.createdAt) as date, COUNT(DISTINCT s.problemId) as solvedCount
          FROM SubmissionEntity s
          JOIN s.videoEntity v
         WHERE v.userId = :userId
           AND s.status = com.climbx.climbx.common.enums.StatusType.ACCEPTED
           AND (:from is Null OR DATE(s.createdAt) >= :from)
           AND (:to is NULL OR DATE(s.createdAt) <= :to)
         GROUP BY DATE(s.createdAt)
         ORDER BY DATE(s.createdAt) ASC
        """)
    List<Object[]> getUserDateSolvedCount(
        @Param("userId") Long userId,
        @Param("from") LocalDate from,
        @Param("to") LocalDate to
    );
}
