package com.climbx.climbx.admin.submissions.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.climbx.climbx.admin.submissions.dto.SubmissionReviewRequestDto;
import com.climbx.climbx.admin.submissions.dto.SubmissionReviewResponseDto;
import com.climbx.climbx.admin.submissions.exception.StatusModifyToPendingException;
import com.climbx.climbx.common.enums.StatusType;
import com.climbx.climbx.submission.entity.SubmissionEntity;
import com.climbx.climbx.submission.exception.PendingSubmissionNotFoundException;
import com.climbx.climbx.submission.repository.SubmissionRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminSubmissionService 테스트")
class AdminSubmissionServiceTest {

    @InjectMocks
    private AdminSubmissionService adminSubmissionService;

    @Mock
    private SubmissionRepository submissionRepository;

    @Nested
    @DisplayName("reviewSubmission 메서드 테스트")
    class ReviewSubmissionTest {

        @Test
        @DisplayName("유효한 PENDING 상태의 제출을 ACCEPTED로 승인하면 성공한다")
        void givenValidPendingSubmission_whenReviewWithAccepted_thenSuccess() {
            // Given
            UUID videoId = UUID.randomUUID();
            String reason = "승인 완료";
            SubmissionReviewRequestDto request = SubmissionReviewRequestDto.builder()
                .status(StatusType.ACCEPTED)
                .reason(reason)
                .build();

            SubmissionEntity submission = SubmissionEntity.builder()
                .videoId(videoId)
                .status(StatusType.PENDING)
                .build();

            given(submissionRepository.findById(videoId))
                .willReturn(Optional.of(submission));

            // When
            SubmissionReviewResponseDto result = adminSubmissionService.reviewSubmission(videoId,
                request);

            // Then
            assertThat(result.videoId()).isEqualTo(videoId);
            assertThat(result.status()).isEqualTo(StatusType.ACCEPTED);
            assertThat(result.reason()).isEqualTo(reason);

            then(submissionRepository).should(times(1)).findById(videoId);
        }

        @Test
        @DisplayName("유효한 PENDING 상태의 제출을 REJECTED로 거절하면 성공한다")
        void givenValidPendingSubmission_whenReviewWithRejected_thenSuccess() {
            // Given
            UUID videoId = UUID.randomUUID();
            String reason = "부적절한 내용";
            SubmissionReviewRequestDto request = SubmissionReviewRequestDto.builder()
                .status(StatusType.REJECTED)
                .reason(reason)
                .build();

            SubmissionEntity submission = SubmissionEntity.builder()
                .videoId(videoId)
                .status(StatusType.PENDING)
                .build();

            given(submissionRepository.findById(videoId))
                .willReturn(Optional.of(submission));

            // When
            SubmissionReviewResponseDto result = adminSubmissionService.reviewSubmission(videoId,
                request);

            // Then
            assertThat(result.videoId()).isEqualTo(videoId);
            assertThat(result.status()).isEqualTo(StatusType.REJECTED);
            assertThat(result.reason()).isEqualTo(reason);

            then(submissionRepository).should(times(1)).findById(videoId);
        }

        @Test
        @DisplayName("존재하지 않는 비디오 ID로 요청하면 PendingSubmissionNotFoundException이 발생한다")
        void givenNonExistentVideoId_whenReview_thenThrowPendingSubmissionNotFoundException() {
            // Given
            UUID videoId = UUID.randomUUID();
            SubmissionReviewRequestDto request = SubmissionReviewRequestDto.builder()
                .status(StatusType.ACCEPTED)
                .reason("승인")
                .build();

            given(submissionRepository.findById(videoId))
                .willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> adminSubmissionService.reviewSubmission(videoId, request))
                .isInstanceOf(PendingSubmissionNotFoundException.class);

            then(submissionRepository).should(times(1)).findById(videoId);
        }

        @Test
        @DisplayName("PENDING이 아닌 상태의 제출을 리뷰하면 PendingSubmissionNotFoundException이 발생한다")
        void givenNonPendingSubmission_whenReview_thenThrowPendingSubmissionNotFoundException() {
            // Given
            UUID videoId = UUID.randomUUID();
            SubmissionReviewRequestDto request = SubmissionReviewRequestDto.builder()
                .status(StatusType.ACCEPTED)
                .reason("승인")
                .build();

            SubmissionEntity submission = SubmissionEntity.builder()
                .videoId(videoId)
                .status(StatusType.ACCEPTED) // 이미 승인된 상태
                .build();

            given(submissionRepository.findById(videoId))
                .willReturn(Optional.of(submission));

            // When & Then
            assertThatThrownBy(() -> adminSubmissionService.reviewSubmission(videoId, request))
                .isInstanceOf(PendingSubmissionNotFoundException.class);

            then(submissionRepository).should(times(1)).findById(videoId);
        }

        @Test
        @DisplayName("REJECTED 상태의 제출을 리뷰하면 PendingSubmissionNotFoundException이 발생한다")
        void givenRejectedSubmission_whenReview_thenThrowPendingSubmissionNotFoundException() {
            // Given
            UUID videoId = UUID.randomUUID();
            SubmissionReviewRequestDto request = SubmissionReviewRequestDto.builder()
                .status(StatusType.ACCEPTED)
                .reason("승인")
                .build();

            SubmissionEntity submission = SubmissionEntity.builder()
                .videoId(videoId)
                .status(StatusType.REJECTED) // 이미 거절된 상태
                .build();

            given(submissionRepository.findById(videoId))
                .willReturn(Optional.of(submission));

            // When & Then
            assertThatThrownBy(() -> adminSubmissionService.reviewSubmission(videoId, request))
                .isInstanceOf(PendingSubmissionNotFoundException.class);

            then(submissionRepository).should(times(1)).findById(videoId);
        }

        @Test
        @DisplayName("검토 상태를 PENDING으로 변경하려고 하면 StatusModifyToPendingException이 발생한다")
        void givenPendingStatus_whenReview_thenThrowStatusModifyToPendingException() {
            // Given
            UUID videoId = UUID.randomUUID();
            SubmissionReviewRequestDto request = SubmissionReviewRequestDto.builder()
                .status(StatusType.PENDING)
                .reason("PENDING으로 변경 시도")
                .build();

            // When & Then
            assertThatThrownBy(() -> adminSubmissionService.reviewSubmission(videoId, request))
                .isInstanceOf(StatusModifyToPendingException.class);

            // Repository 호출이 없어야 함 (예외가 먼저 발생하므로)
            then(submissionRepository).should(times(0)).findById(videoId);
        }
    }
}
