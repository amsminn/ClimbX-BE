package com.climbx.climbx.problem;

import com.climbx.climbx.gym.entity.GymEntity;
import com.climbx.climbx.gym.repository.GymRepository;
import com.climbx.climbx.problem.dto.ProblemInfoInSpotResponseDto;
import com.climbx.climbx.problem.dto.SpotDetailsResponseDto;
import com.climbx.climbx.problem.dto.SpotResponseDto;
import com.climbx.climbx.problem.repository.ProblemRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final GymRepository gymRepository;

    public SpotResponseDto getProblemSpotsWithFilters(
        Long gymId,
        String localLevel,
        String holdColor
    ) {
        // Gym 정보 조회
        Optional<GymEntity> gym = gymRepository.findById(gymId);

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
            .map2dUrl(gym.map(GymEntity::map2dUrl).orElse(null))
            .spotDetailsResponseDtoList(spotDetailsResponseDtoList)
            .build();
    }
}
