package com.climbx.climbx.problem;

import com.climbx.climbx.common.error.ErrorCode;
import com.climbx.climbx.gym.entity.GymEntity;
import com.climbx.climbx.gym.exception.GymNotFoundException;
import com.climbx.climbx.gym.repository.GymRepository;
import com.climbx.climbx.problem.dto.ProblemDetailsResponseDto;
import com.climbx.climbx.problem.dto.SpotDetailsResponseDto;
import com.climbx.climbx.problem.dto.SpotResponseDto;
import com.climbx.climbx.problem.repository.ProblemRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final GymRepository gymRepository;

    @Transactional(readOnly = true)
    public SpotResponseDto getProblemSpotsWithFilters(
        Long gymId,
        String localLevel,
        String holdColor
    ) {

        // 파라미터가 하나라도 null인 경우 throw
        if (gymId == null || localLevel == null || holdColor == null) {
            throw new IllegalArgumentException(ErrorCode.INVALID_REQUEST.message());
        }

        // Gym 정보 조회
        GymEntity gym = gymRepository.findById(gymId)
            .orElseThrow(() -> new GymNotFoundException(gymId));

        // 필터링된 문제들 조회
        List<ProblemDetailsResponseDto> problems = problemRepository
            .findByGym_GymIdAndLocalLevelAndHoldColor(gymId, localLevel, holdColor)
            .stream()
            .map(ProblemDetailsResponseDto::from)
            .toList();

        // 문제들을 spotId로 그룹화
        // {spotId, [problem1, problem2, ...]}
        Map<Long, List<ProblemDetailsResponseDto>> groupedProblems = problems.stream()
            .collect(Collectors.groupingBy(ProblemDetailsResponseDto::spotId));

        // 그룹화된 문제들을 SpotDetailsResponseDto로 변환
        // [{spotId, [problem1, problem2, ...]}, {spotId, [problem3, problem4, ...]}]
        List<SpotDetailsResponseDto> spotDetailsResponseDtoList = groupedProblems.entrySet()
            .stream()
            .map(entry -> {
                Long spotId = entry.getKey();
                List<ProblemDetailsResponseDto> problemDetailsResponseDtoList = entry.getValue();
                return SpotDetailsResponseDto.builder()
                    .spotId(spotId)
                    .problemDetailsResponseDtoList(problemDetailsResponseDtoList)
                    .build();
            }).toList();

        // 결과 DTO 반환
        return SpotResponseDto.builder()
            .gymId(gymId)
            .map2dUrl(gym.map2dUrl())
            .spotDetailsResponseDtoList(spotDetailsResponseDtoList)
            .build();
    }
}
