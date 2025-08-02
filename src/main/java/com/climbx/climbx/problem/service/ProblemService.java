package com.climbx.climbx.problem.service;

import com.climbx.climbx.common.enums.ActiveStatusType;
import com.climbx.climbx.common.service.S3Service;
import com.climbx.climbx.gym.entity.GymEntity;
import com.climbx.climbx.gym.exception.GymNotFoundException;
import com.climbx.climbx.gym.repository.GymRepository;
import com.climbx.climbx.problem.dto.ProblemCreateRequestDto;
import com.climbx.climbx.problem.dto.ProblemCreateResponseDto;
import com.climbx.climbx.problem.dto.ProblemInfoInSpotResponseDto;
import com.climbx.climbx.problem.dto.SpotDetailsResponseDto;
import com.climbx.climbx.problem.dto.SpotResponseDto;
import com.climbx.climbx.problem.entity.GymAreaEntity;
import com.climbx.climbx.problem.entity.ProblemEntity;
import com.climbx.climbx.problem.exception.GymAreaNotFoundException;
import com.climbx.climbx.problem.repository.GymAreaRepository;
import com.climbx.climbx.problem.repository.ProblemRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
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

    public SpotResponseDto getProblemSpotsWithFilters(
        Long gymId,
        String localLevel,
        String holdColor
    ) {
        // Gym 정보 조회
        GymEntity gym = gymRepository.findById(gymId)
            .orElseThrow(() -> new GymNotFoundException(gymId));

        // 필터링된 문제들 조회
        List<ProblemInfoInSpotResponseDto> problems = problemRepository
            .findByGym_GymIdAndLocalLevelAndHoldColor(gymId, localLevel, holdColor)
            .stream()
            .map(ProblemInfoInSpotResponseDto::from)
            .toList();

        // 문제들을 spotId로 그룹화
        // {spotId, [problem1, problem2, ...]}
        Map<Long, List<ProblemInfoInSpotResponseDto>> groupedProblems = problems.stream()
            .collect(Collectors.groupingBy(ProblemInfoInSpotResponseDto::spotId));

        // 그룹화된 문제들을 SpotDetailsResponseDto로 변환
        // [{spotId, [problem1, problem2, ...]}, {spotId, [problem3, problem4, ...]}]
        List<SpotDetailsResponseDto> spotDetailsResponseDtoList = groupedProblems.entrySet()
            .stream()
            .map(entry -> {
                Long spotId = entry.getKey();
                List<ProblemInfoInSpotResponseDto> problemInfoInSpotResponseDtoList = entry.getValue();
                return SpotDetailsResponseDto.builder()
                    .spotId(spotId)
                    .problemDetailsResponseDtoList(problemInfoInSpotResponseDtoList)
                    .build();
            }).toList();

        // 결과 DTO 반환
        return SpotResponseDto.builder()
            .gymId(gymId)
            .map2dUrl(gym.map2dUrl())
            .spotDetailsResponseDtoList(spotDetailsResponseDtoList)
            .build();
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
            .spotId(request.spotId())
            .spotXRatio(request.spotXRatio())
            .spotYRatio(request.spotYRatio())
            .problemImageCdnUrl(imageCdnUrl)
            .status(ActiveStatusType.ACTIVE)
            .build();

        ProblemEntity savedProblem = problemRepository.save(problem);

        log.info("Problem created successfully: problemId={}, imageCdnUrl={}",
            savedProblem.problemId(), imageCdnUrl);

        return ProblemCreateResponseDto.from(savedProblem);
    }
}
