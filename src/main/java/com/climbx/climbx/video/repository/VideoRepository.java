package com.climbx.climbx.video.repository;

import com.climbx.climbx.video.entity.VideoEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<VideoEntity, UUID> {

    /**
     * 특정 사용자의 특정 상태 비디오를 생성일 역순으로 조회 (@SQLRestriction 자동 적용)
     */
    List<VideoEntity> findByUserIdAndStatusOrderByCreatedAtDesc(
        Long userId,
        String status
    );

    /**
     * 특정 사용자의 모든 비디오를 조회합니다 (soft delete 포함).
     */
    List<VideoEntity> findByUserId(Long userId);
} 
