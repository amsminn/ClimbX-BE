package com.climbx.climbx.admin.submissions.service;

import com.climbx.climbx.common.service.S3Service;
import com.climbx.climbx.gym.dto.Gym2dMapInfo;
import com.climbx.climbx.gym.entity.GymEntity;
import com.climbx.climbx.gym.exception.GymNotFoundException;
import com.climbx.climbx.gym.repository.GymRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminGymService {

    private final S3Service s3Service;
    private final GymRepository gymRepository;

    @Transactional
    public void uploadGym2dMap(
        Long gymId,
        MultipartFile baseImage,
        List<MultipartFile> overlayImages
    ) {
        log.info("클라이밍장 2D 맵 업로드 시작: gymId={}, baseImage={}, overlayMapImages={}",
            gymId, baseImage.getOriginalFilename(), overlayImages.size());

        // 클라이밍장 존재 여부 확인
        GymEntity gym = gymRepository.findById(gymId)
            .orElseThrow(() -> new GymNotFoundException(gymId));

        // S3에 이미지들 업로드하고 CDN URL들 받아오기
        Gym2dMapInfo gym2dMapInfo = s3Service.uploadGym2dMapImages(gymId, baseImage, overlayImages);

        gym.setMap2dUrls(gym2dMapInfo);

        log.info("클라이밍장 2D 맵 업로드 완료: gymId={}, baseMapUrl={}, overlayMapUrls={}",
            gymId, gym2dMapInfo.baseMapUrl(), gym2dMapInfo.overlayMapUrls().size());
    }
}
