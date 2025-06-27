package com.climbx.climbx.user.repository;

import com.climbx.climbx.common.enums.UserHistoryCriteriaType;
import com.climbx.climbx.user.entity.UserRankingHistoryEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRankingHistoryRepository extends JpaRepository<UserRankingHistoryEntity, Long> {

    /**
     * 사용자가 특정 기간 동안 특정 criteria에 대한 일별 히스토리를 조회
     */
    @Query("""
        SELECT DATE(h.createdAt) as date, h.value
          FROM UserRankingHistoryEntity h
         WHERE h.userId = :userId
           AND h.part = :criteria
           AND (:from IS NULL OR DATE(h.createdAt) >= :from)
           AND (:to IS NULL OR DATE(h.createdAt) <= :to)
         ORDER BY DATE(h.createdAt) ASC
        """)
    List<Object[]> getUserDailyHistory(
        @Param("userId") Long userId,
        @Param("criteria") UserHistoryCriteriaType criteria,
        @Param("from") LocalDate from,
        @Param("to") LocalDate to
    );
}
