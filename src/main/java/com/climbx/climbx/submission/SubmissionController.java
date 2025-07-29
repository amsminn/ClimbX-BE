package com.climbx.climbx.submission;

import com.climbx.climbx.common.annotation.SuccessStatus;
import com.climbx.climbx.submission.dto.SubmissionAppealResponseDto;
import com.climbx.climbx.submission.dto.SubmissionCancelResponseDto;
import com.climbx.climbx.submission.dto.SubmissionCreateRequestDto;
import com.climbx.climbx.submission.dto.SubmissionListResponseDto;
import com.climbx.climbx.submission.dto.SubmissionResponseDto;
import com.climbx.climbx.submission.service.SubmissionService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController implements SubmissionApiDocumentation {

    private final SubmissionService submissionService;

    @Override
    @GetMapping
    @SuccessStatus(value = HttpStatus.OK)
    public SubmissionListResponseDto getSubmissions(
        @RequestParam(name = "userId", required = false)
        Long userId,

        @RequestParam(name = "problemId", required = false)
        Long problemId,

        @RequestParam(name = "holdColor", required = false)
        String holdColor,

        @RequestParam(name = "ratingFrom", required = false)
        Integer ratingFrom,

        @RequestParam(name = "ratingTo", required = false)
        Integer ratingTo,

        @RequestParam(name = "order", required = false, defaultValue = "desc")
        String order,

        @RequestParam(name = "page", required = false, defaultValue = "0")
        Integer page,

        @RequestParam(name = "perPage", required = false, defaultValue = "20")
        Integer perPage
    ) {
        return submissionService.getSubmissions(
            userId,
            problemId,
            holdColor,
            ratingFrom,
            ratingTo,
            order,
            page,
            perPage
        );
    }

    @Override
    @PostMapping
    @SuccessStatus(value = HttpStatus.CREATED)
    public SubmissionResponseDto createSubmission(
        @AuthenticationPrincipal
        Long userId,

        @RequestBody
        SubmissionCreateRequestDto request
    ) {
        return submissionService.createSubmission(userId, request);
    }

    @Override
    @GetMapping("/{videoId}")
    @SuccessStatus(value = HttpStatus.OK)
    public SubmissionResponseDto getSubmissionByVideoId(
        @PathVariable(name = "videoId")
        UUID videoId
    ) {
        return submissionService.getSubmissionByVideoId(videoId);
    }

    @Override
    @PatchMapping("/{videoId}")
    @SuccessStatus(value = HttpStatus.OK)
    public SubmissionCancelResponseDto cancelSubmission(
        @AuthenticationPrincipal
        Long userId,

        @PathVariable(name = "videoId")
        UUID videoId
    ) {
        return submissionService.cancelSubmission(userId, videoId);
    }

    @Override
    @GetMapping("/{videoId}/appeal")
    @SuccessStatus(value = HttpStatus.OK)
    public SubmissionAppealResponseDto getSubmissionAppeal(
        @PathVariable(name = "videoId")
        UUID videoId
    ) {
        return submissionService.getSubmissionAppeal(videoId);
    }

    @Override
    @PostMapping("/{videoId}/appeal")
    @SuccessStatus(value = HttpStatus.CREATED)
    public SubmissionAppealResponseDto appealSubmission(
        @AuthenticationPrincipal
        Long userId,

        @PathVariable(name = "videoId")
        UUID videoId,

        @RequestBody(required = false)
        String reason // Optional reason for appeal
    ) {
        return submissionService.appealSubmission(userId, videoId, reason);
    }
}
