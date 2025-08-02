package com.climbx.climbx.admin.submissions.service;

import com.climbx.climbx.admin.submissions.dto.SubmissionReviewRequestDto;
import com.climbx.climbx.admin.submissions.dto.SubmissionReviewResponseDto;
import com.climbx.climbx.admin.submissions.exception.StatusModifyToPendingException;
import com.climbx.climbx.common.enums.StatusType;
import com.climbx.climbx.submission.entity.SubmissionEntity;
import com.climbx.climbx.submission.exception.PendingSubmissionNotFoundException;
import com.climbx.climbx.submission.repository.SubmissionRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AdminSubmissionService {

    private final SubmissionRepository submissionRepository;

    @Transactional
    public SubmissionReviewResponseDto reviewSubmission(
        UUID videoId,
        SubmissionReviewRequestDto request
    ) {
        // 검토 요청 상태가 PENDING인 경우 예외 처리
        Optional.of(request.status())
            .filter(s -> s.equals(StatusType.PENDING))
            .ifPresent(s -> {
                log.warn("Status를 PENDING으로 변경 시도 videoId: {}, status: {}, reason: {}",
                    videoId, request.status(), request.reason());
                throw new StatusModifyToPendingException(videoId);
            });

        // PENDING 상태의 제출 조회
        SubmissionEntity submission = submissionRepository.findById(videoId)
            .filter(s -> s.status().equals(StatusType.PENDING))
            .orElseThrow(() -> new PendingSubmissionNotFoundException(videoId));

        submission.setStatus(request.status(), request.reason());

        log.info("Reviewing succeed: videoId: {}, status: {}, reason: {}",
            submission.videoId(), submission.status(), submission.statusReason());

        return SubmissionReviewResponseDto.builder()
            .videoId(submission.videoId())
            .status(submission.status())
            .reason(submission.statusReason())
            .build();
    }
}
