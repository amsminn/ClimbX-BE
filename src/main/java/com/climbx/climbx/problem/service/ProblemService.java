package com.climbx.climbx.problem.service;

import com.climbx.climbx.common.enums.ActiveStatusType;
import com.climbx.climbx.common.service.S3Service;
import com.climbx.climbx.gym.entity.GymEntity;
import com.climbx.climbx.gym.exception.GymNotFoundException;
import com.climbx.climbx.gym.repository.GymRepository;
import com.climbx.climbx.problem.dto.ProblemCreateRequestDto;
import com.climbx.climbx.problem.dto.ProblemCreateResponseDto;
import com.climbx.climbx.problem.dto.ProblemInfoResponseDto;
import com.climbx.climbx.problem.entity.GymAreaEntity;
import com.climbx.climbx.problem.entity.ProblemEntity;
import com.climbx.climbx.problem.exception.GymAreaNotFoundException;
import com.climbx.climbx.problem.repository.GymAreaRepository;
import com.climbx.climbx.problem.repository.ProblemRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final GymRepository gymRepository;
    private final GymAreaRepository gymAreaRepository;
    private final S3Service s3Service;

    public List<ProblemInfoResponseDto> getProblemsWithFilters(
        Long gymId,
        Long gymAreaId,
        String localLevel,
        String holdColor,
        ActiveStatusType activeStatus
    ) {
        // Gym 정보 조회
        GymEntity gym = gymRepository.findById(gymId)
            .orElseThrow(() -> new GymNotFoundException(gymId));

        // GymArea 정보 조회
        GymAreaEntity gymArea = gymAreaRepository.findById(gymAreaId)
            .orElseThrow(() -> new GymAreaNotFoundException(gymAreaId));

        // 필터링된 문제들 조회
        List<ProblemInfoResponseDto> problems = problemRepository
            .findByGymAndAreaAndLevelAndColorAndActiveStatus(gymId, gymAreaId, localLevel,
                holdColor, activeStatus)
            .stream()
            .map(problem -> ProblemInfoResponseDto.from(problem, gymId, gymArea))
            .toList();

        return problems;
    }

    @Transactional
    public ProblemCreateResponseDto registerProblem(
        ProblemCreateRequestDto request,
        MultipartFile problemImage
    ) {
        log.info("Creating problem: gymAreaId={}, localLevel={}, holdColor={}",
            request.gymAreaId(), request.localLevel(), request.holdColor());

        // GymArea 조회
        GymAreaEntity gymArea = gymAreaRepository.findById(request.gymAreaId())
            .orElseThrow(() -> new GymAreaNotFoundException(request.gymAreaId()));

        // Gym 정보는 GymArea를 통해 가져옴
        GymEntity gym = gymArea.gym();

        // 이미지 업로드 처리
        UUID problemId = UUID.randomUUID();
        String imageCdnUrl = null;
        if (problemImage != null && !problemImage.isEmpty()) {
            imageCdnUrl = s3Service.uploadProblemImage(problemId, gymArea.gymAreaId(),
                problemImage);
        }

        // Problem 엔티티 생성
        ProblemEntity problem = ProblemEntity.builder()
            .problemId(problemId)
            .gym(gym)
            .gymArea(gymArea)
            .localLevel(request.localLevel())
            .holdColor(request.holdColor())
            .problemRating(request.problemRating())
            .problemImageCdnUrl(imageCdnUrl)
            .activeStatus(ActiveStatusType.ACTIVE)
            .build();

        ProblemEntity savedProblem = problemRepository.save(problem);

        log.info("Problem created successfully: problemId={}, imageCdnUrl={}",
            savedProblem.problemId(), imageCdnUrl);

        return ProblemCreateResponseDto.from(savedProblem);
    }
}
