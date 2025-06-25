package com.climbx.climbx.submission.repository;

import com.climbx.climbx.common.enums.StatusType;
import com.climbx.climbx.submission.entity.SubmissionEntity;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<SubmissionEntity, Long> {

    List<SubmissionEntity> findAllByUserIdAndStatusOrderByDifficultyDesc(Long userId, StatusType status, Pageable pageable);

    default List<SubmissionEntity> findTopProblemsByUserId(Long userId, Integer limit) {
        return findAllByUserIdAndStatusOrderByDifficultyDesc(userId, StatusType.ACCEPTED, Pageable.ofSize(limit))
            .stream()
            .limit(limit)
            .toList();
    }
}
