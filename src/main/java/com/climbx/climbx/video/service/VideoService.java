package com.climbx.climbx.video.service;

import com.climbx.climbx.common.comcode.ComcodeService;
import com.climbx.climbx.common.util.SecurityContextUtils;
import com.climbx.climbx.user.entity.UserAccountEntity;
import com.climbx.climbx.user.exception.UserNotFoundException;
import com.climbx.climbx.user.repository.UserAccountRepository;
import com.climbx.climbx.video.dto.VideoUploadRequestDto;
import com.climbx.climbx.video.dto.VideoUploadResponseDto;
import com.climbx.climbx.video.entity.VideoEntity;
import com.climbx.climbx.video.repository.VideoRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final S3Service s3Service;
    private final ComcodeService comcodeService;
    private final VideoRepository videoRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional
    public VideoUploadResponseDto createVideoUploadUrl(
        VideoUploadRequestDto videoUploadRequestDto) {

        Long userId = SecurityContextUtils.getCurrentUserId();
        UserAccountEntity user = userAccountRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        UUID videoId = UUID.randomUUID();
        String presignedUrl = s3Service.generatePresignedUrl(
            videoId,
            videoUploadRequestDto.fileExtension()
        );

        VideoEntity videoEntity = VideoEntity.builder()
            .videoId(videoId)
            .userId(userId)
            .userAccountEntity(user)
            .status(comcodeService.getCodeValue("PENDING"))
            .build();

        videoRepository.save(videoEntity);

        return VideoUploadResponseDto.builder()
            .videoId(videoId)
            .presignedUrl(presignedUrl)
            .build();
    }
}
