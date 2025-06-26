package com.climbx.climbx.submission.repository;

import com.climbx.climbx.submission.entity.SubmissionEntity;
import com.climbx.climbx.submission.repository.custom.SubmissionRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends SubmissionRepositoryCustom,
    JpaRepository<SubmissionEntity, Long> {

    // Define custom query methods here if needed
    // For example, you might want to add methods for finding submissions by user, route, etc.
    // Example:
    // List<SubmissionEntity> findByUserId(Long userId);
    // List<SubmissionEntity> findByRouteId(Long routeId);

}
