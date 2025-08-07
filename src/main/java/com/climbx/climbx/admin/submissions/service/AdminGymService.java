package com.climbx.climbx.admin.submissions.service;

import com.climbx.climbx.common.service.S3Service;
import com.climbx.climbx.gym.entity.GymAreaEntity;
import com.climbx.climbx.gym.entity.GymEntity;
import com.climbx.climbx.gym.exception.GymNotFoundException;
import com.climbx.climbx.gym.repository.GymAreaRepository;
import com.climbx.climbx.gym.repository.GymRepository;
import com.climbx.climbx.problem.exception.GymAreaNotFoundException;
import java.util.Map;
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
    private final GymAreaRepository gymAreaRepository;

    @Transactional
    public void uploadGym2dMap(
        Long gymId,
        MultipartFile map2dImage,
        Map<Long, MultipartFile> areaImages
    ) {
        log.info("클라이밍장 2D 맵 업로드 시작: gymId={}, map2dImage={}, areaImage.size={}",
            gymId, map2dImage.getOriginalFilename(), areaImages.size());

        // 클라이밍장 존재 여부 확인
        GymEntity gym = gymRepository.findById(gymId)
            .orElseThrow(() -> new GymNotFoundException(gymId));

        // S3에 클라이밍장 2D 맵 이미지 업로드
        String map2dImageCdnUrl = s3Service.uploadGym2dMapImages(gymId, map2dImage);
        gym.setMap2dImageCdnUrl(map2dImageCdnUrl);

        // S3에 Area(벽) 이미지 업로드
        Map<Long, String> areaImageCdnUrls = s3Service.uploadGymAreaImages(gymId, areaImages);
        areaImageCdnUrls.forEach((areaId, cdnUrl) -> {
            GymAreaEntity gymArea = gymAreaRepository.findById(areaId)
                .orElseThrow(() -> new GymAreaNotFoundException(areaId));
            gymArea.setAreaImageCdnUrl(cdnUrl);
        });

        log.info("클라이밍장, 벽 2D 맵 업로드 완료: gymId={}, map2dImageCdnUrl={}, areaImageCdnUrls.size={}",
            gymId, map2dImage, areaImageCdnUrls.size());
    }
}
