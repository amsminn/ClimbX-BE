package com.climbx.climbx.video.dto;

import com.climbx.climbx.video.entity.VideoEntity;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record VideoListResponseDto(

    String thumbnailCdnUrl,
    String hlsCdnUrl,
    String status,
    Integer durationSeconds,
    LocalDateTime createdAt
) {

    public static VideoListResponseDto from(VideoEntity videoEntity) {
        return VideoListResponseDto.builder()
            .thumbnailCdnUrl(videoEntity.thumbnailCdnUrl())
            .hlsCdnUrl(videoEntity.hlsCdnUrl())
            .status(videoEntity.status())
            .durationSeconds(videoEntity.durationSeconds())
            .createdAt(videoEntity.getCreatedAt())
            .build();
    }
}