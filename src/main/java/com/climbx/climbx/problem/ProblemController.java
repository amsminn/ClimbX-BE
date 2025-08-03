package com.climbx.climbx.problem;

import com.climbx.climbx.common.annotation.SuccessStatus;
import com.climbx.climbx.common.enums.ActiveStatusType;
import com.climbx.climbx.problem.dto.ProblemCreateRequestDto;
import com.climbx.climbx.problem.dto.ProblemCreateResponseDto;
import com.climbx.climbx.problem.dto.ProblemInfoResponseDto;
import com.climbx.climbx.problem.service.ProblemService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController implements ProblemApiDocumentation {

    private final ProblemService problemService;

    @Override
    @GetMapping
    @SuccessStatus(value = HttpStatus.OK)
    public List<ProblemInfoResponseDto> getProblemsWithFilters(
        @RequestParam(value = "gymId")
        Long gymId,

        @RequestParam(value = "gymAreaId", required = true)
        Long gymAreaId,

        @RequestParam(value = "localLevel", required = true)
        String localLevel,

        @RequestParam(value = "holdColor", required = true)
        String holdColor,

        @RequestParam(value = "activeStatus", required = true)
        ActiveStatusType activeStatus
    ) {
        log.info("문제 조회: gymId={}, gymAreaId={}, localLevel={}, holdColor={}, activeStatus={}",
            gymId, gymAreaId, localLevel, holdColor, activeStatus);
        return problemService.getProblemsWithFilters(gymId, gymAreaId, localLevel, holdColor,
            activeStatus);
    }

    @Override
    @PostMapping
    @SuccessStatus(value = HttpStatus.CREATED)
    public ProblemCreateResponseDto registerProblem(
        @RequestPart("request") @Valid ProblemCreateRequestDto request,
        @RequestPart(value = "problemImage", required = false) MultipartFile problemImage
    ) {
        log.info("문제 생성: gymAreaId={}, localLevel={}, holdColor={}",
            request.gymAreaId(), request.localLevel(), request.holdColor());
        return problemService.registerProblem(request, problemImage);
    }
} 