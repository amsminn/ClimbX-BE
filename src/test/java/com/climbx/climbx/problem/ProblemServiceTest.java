package com.climbx.climbx.problem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.climbx.climbx.fixture.GymFixture;
import com.climbx.climbx.fixture.ProblemFixture;
import com.climbx.climbx.gym.entity.GymEntity;
import com.climbx.climbx.gym.repository.GymRepository;
import com.climbx.climbx.problem.dto.SpotResponseDto;
import com.climbx.climbx.problem.entity.ProblemEntity;
import com.climbx.climbx.problem.repository.ProblemRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProblemServiceTest {

    @Mock
    private ProblemRepository problemRepository;
    @Mock
    private GymRepository gymRepository;

    @InjectMocks
    private ProblemService problemService;

    @Nested
    @DisplayName("문제 검색 조건별 테스트")
    class GetProblemSpotsWithFilters {

        @Test
        @DisplayName("gymId, localLevel, holdColor 모든 조건이 주어지면, 해당 조건에 맞는 문제들을 spotId로 그룹화하여 반환한다")
        void getProblemSpotsWithAllFilters() {
            // given
            Long gymId = 1L;
            String localLevel = "빨강";
            String holdColor = "파랑";

            GymEntity gymEntity = GymFixture.createGymEntity(gymId, "테스트 클라이밍장", 37.0, 126.0);
            ProblemEntity problemEntity1 = ProblemFixture.createProblemEntity(1L, gymEntity,
                localLevel, holdColor, 1200, 1L, 50.0, 30.0);
            ProblemEntity problemEntity2 = ProblemFixture.createProblemEntity(2L, gymEntity,
                localLevel, holdColor, 1300, 1L, 55.0, 35.0);
            ProblemEntity problemEntity3 = ProblemFixture.createProblemEntity(3L, gymEntity,
                localLevel, holdColor, 1400, 2L, 60.0, 40.0);

            List<ProblemEntity> mockProblems = List.of(problemEntity1, problemEntity2,
                problemEntity3);

            given(gymRepository.findById(gymId))
                .willReturn(Optional.of(gymEntity));
            given(problemRepository.findByGym_GymIdAndLocalLevelAndHoldColor(
                gymId, localLevel, holdColor
            )).willReturn(mockProblems);

            // when
            SpotResponseDto result = problemService.getProblemSpotsWithFilters(
                gymId, localLevel, holdColor);

            // then
            then(problemRepository).should(times(1))
                .findByGym_GymIdAndLocalLevelAndHoldColor(gymId, localLevel, holdColor);

            assertThat(result.spotDetailsResponseDtoList()).hasSize(2); // spotId 1, 2로 그룹화됨
            assertThat(
                result.spotDetailsResponseDtoList().get(0).problemDetailsResponseDtoList())
                .hasSize(2); // spotId 1에 2개 문제
            assertThat(result.spotDetailsResponseDtoList().get(1).problemDetailsResponseDtoList())
                .hasSize(1); // spotId 2에 1개 문제
        }
    }
} 