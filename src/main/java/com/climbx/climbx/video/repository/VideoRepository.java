package com.climbx.climbx.video.repository;

import com.climbx.climbx.video.entity.VideoEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<VideoEntity, UUID> {

    List<VideoEntity> findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long userId);
} 
