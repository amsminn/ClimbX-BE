package com.climbx.climbx.problem;

import com.climbx.climbx.common.annotation.SuccessStatus;
import com.climbx.climbx.problem.dto.SpotResponseDto;
import com.climbx.climbx.problem.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController implements ProblemApiDocumentation {

    private final ProblemService problemService;

    @Override
    @GetMapping
    @SuccessStatus(value = HttpStatus.OK)
    public SpotResponseDto getProblemSpotsWithFilters(
        @RequestParam(value = "gymId", required = true)
        Long gymId,

        @RequestParam(value = "localLevel", required = true)
        String localLevel,

        @RequestParam(value = "holdColor", required = true)
        String holdColor
    ) {
        return problemService.getProblemSpotsWithFilters(gymId, localLevel, holdColor);
    }
} 