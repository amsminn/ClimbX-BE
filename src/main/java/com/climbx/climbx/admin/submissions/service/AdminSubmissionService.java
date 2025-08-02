package com.climbx.climbx.admin.submissions.service;

import com.climbx.climbx.admin.submissions.dto.SubmissionReviewRequestDto;
import com.climbx.climbx.admin.submissions.dto.SubmissionReviewResponseDto;
import com.climbx.climbx.common.enums.StatusType;
import com.climbx.climbx.submission.entity.SubmissionEntity;
import com.climbx.climbx.submission.exception.PendingSubmissionNotFoundException;
import com.climbx.climbx.submission.repository.SubmissionRepository;
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

        // PENDING 상태의 제출 조회
        SubmissionEntity submission = submissionRepository.findById(videoId)
            .filter(s -> s.status().equals(StatusType.PENDING))
            .orElseThrow(() -> new PendingSubmissionNotFoundException(videoId));

        submission.setStatus(request.status(), request.reason());

        return SubmissionReviewResponseDto.builder()
            .videoId(submission.videoId())
            .status(submission.status())
            .reason(submission.statusReason())
            .build();
    }
}
