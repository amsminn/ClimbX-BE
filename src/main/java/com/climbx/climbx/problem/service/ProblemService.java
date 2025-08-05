package com.climbx.climbx.problem.service;

import com.climbx.climbx.common.enums.ActiveStatusType;
import com.climbx.climbx.common.enums.ErrorCode;
import com.climbx.climbx.common.enums.StatusType;
import com.climbx.climbx.common.exception.InvalidParameterException;
import com.climbx.climbx.common.service.S3Service;
import com.climbx.climbx.gym.entity.GymAreaEntity;
import com.climbx.climbx.gym.entity.GymEntity;
import com.climbx.climbx.gym.repository.GymAreaRepository;
import com.climbx.climbx.gym.repository.GymRepository;
import com.climbx.climbx.problem.dto.ProblemCreateRequestDto;
import com.climbx.climbx.problem.dto.ProblemCreateResponseDto;
import com.climbx.climbx.problem.dto.ProblemInfoResponseDto;
import com.climbx.climbx.problem.dto.ProblemVoteRequestDto;
import com.climbx.climbx.problem.entity.GymAreaEntity;
import com.climbx.climbx.problem.entity.ProblemEntity;
import com.climbx.climbx.problem.enums.ProblemTierType;
import com.climbx.climbx.problem.exception.ForbiddenProblemVoteException;
import com.climbx.climbx.problem.exception.GymAreaNotFoundException;
import com.climbx.climbx.problem.repository.ProblemRepository;
import com.climbx.climbx.submission.entity.SubmissionEntity;
import com.climbx.climbx.submission.repository.SubmissionRepository;
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
    private final SubmissionRepository submissionRepository;
    private final GymAreaRepository gymAreaRepository;
    private final S3Service s3Service;

    public List<ProblemInfoResponseDto> getProblemsWithFilters(
        Long gymId,
        Long gymAreaId,
        String localLevel,
        String holdColor,
        ProblemTierType problemTier,
        ActiveStatusType activeStatus
    ) {
        // 필터링된 문제들 조회
        return problemRepository.findByGymAndAreaAndLevelAndColorAndProblemTierAndActiveStatus(
            gymId, gymAreaId, localLevel, holdColor, problemTier, activeStatus);
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

        if (problemImage == null || problemImage.isEmpty()) {
            throw new InvalidParameterException(
                ErrorCode.INVALID_PARAMETER, "Problem image must not be null or empty");
        }

        // 이미지 업로드 처리
        UUID problemId = UUID.randomUUID();
        String imageCdnUrl = s3Service.uploadProblemImage(problemId, gymArea.gymAreaId(),
            problemImage);

        // Problem 엔티티 생성
        ProblemEntity problem = ProblemEntity.builder()
            .problemId(problemId)
            .gym(gym)
            .gymArea(gymArea)
            .localLevel(request.localLevel())
            .holdColor(request.holdColor())
            // Todo .problemRating()
            .problemImageCdnUrl(imageCdnUrl)
            .activeStatus(ActiveStatusType.ACTIVE)
            .build();

        ProblemEntity savedProblem = problemRepository.save(problem);

        log.info(
            "Problem created successfully: problemId={}, localLevel={}, holdColor={}, imageCdnUrl={}",
            savedProblem.problemId(), savedProblem.localLevel(), savedProblem.holdColor(),
            imageCdnUrl);

        return ProblemCreateResponseDto.from(savedProblem);
    }

    @Transactional
    public ProblemInfoResponseDto voteProblem(
        Long userId,
        UUID problemId,
        ProblemVoteRequestDto voteRequest
    ) {
        log.info("난이도 투표 요청: userId={}, problemId={}, vote={}",
            userId, problemId, voteRequest);

        SubmissionEntity submission = submissionRepository.findByProblemIdAndVideoEntity_UserIdAndStatus(
            problemId,
            userId,
            StatusType.ACCEPTED
        ).orElseThrow(() -> new ForbiddenProblemVoteException(problemId, userId));

        ProblemEntity problem = submission.problemEntity();

        // 임시 반환. SWM-155 이슈에서 난이도 기여 구현 완료 후 변경 예정
        return ProblemInfoResponseDto.from(problem, 1L, problem.gymArea());
    }
}
