package com.climbx.climbx.gym;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.climbx.climbx.fixture.GymFixture;
import com.climbx.climbx.gym.dto.GymInfoResponseDto;
import com.climbx.climbx.gym.entity.GymEntity;
import com.climbx.climbx.gym.repository.GymRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GymServiceTest {

    @Mock
    private GymRepository gymRepository;

    @InjectMocks
    private GymService gymService;

    @Nested
    @DisplayName("클라이밍장 조회")
    class GetGymList {

        @Test
        @DisplayName("위치 정보가 주어지지 않았을 때, 전체 클라이밍장 목록을 반환한다")
        void getGymList() {
            // given
            GymEntity gymEntity1 = GymFixture.createGymEntity(1L, "Gym1", 37.0, 126.0);
            GymEntity gymEntity2 = GymFixture.createGymEntity(2L, "Gym2", 38.0, 127.0);

            given(gymRepository.findAll())
                .willReturn(List.of(gymEntity1, gymEntity2));

            // when
            List<GymInfoResponseDto> gymList = gymService.getGymList();

            // then
            then(gymRepository).should(times(1)).findAll();
            assertThat(gymList.size()).isEqualTo(2);
            assertThat(gymList.get(0)).isEqualTo(
                GymFixture.createGymInfoResponseDto(1L, "Gym1", 37.0, 126.0));
            assertThat(gymList.get(1)).isEqualTo(
                GymFixture.createGymInfoResponseDto(2L, "Gym2", 38.0, 127.0));
        }

        @DisplayName("전달받은 위치 정보가 유효할 때, 현재 위치와 가까운 순으로 클라이밍장 목록을 반환한다")
        @ParameterizedTest
        @CsvSource({
            // 위도, 경도, 가까운 클라이밍장 ID, 먼 클라이밍장 ID
            "1.0,   50.0,  1,  2", // 첫 번째가 가까운 경우
            "80.0,  170.0,  2,  1"  // 두 번째가 가까운 경우
        })
        void getGymList_whenValidLocation(Double latitude, Double longitude,
            Long firstId, Long secondId) {
            // given
            GymEntity gymEntity1 = GymFixture.createGymEntity(1L, "Gym1", 37.0, 126.0);
            GymEntity gymEntity2 = GymFixture.createGymEntity(2L, "Gym2", 38.0, 127.0);

            // gymEntity1과 gymEntity2의 거리를 기준으로 정렬
            List<GymEntity> sortedGyms = firstId == 1L
                ? List.of(gymEntity1, gymEntity2)
                : List.of(gymEntity2, gymEntity1);

            given(gymRepository.findAllByLocationOrderByDistance(latitude, longitude))
                .willReturn(sortedGyms);

            // when
            List<GymInfoResponseDto> result = gymService.getGymListByDistance(latitude, longitude);

            // then
            then(gymRepository)
                .should(times(1))
                .findAllByLocationOrderByDistance(latitude, longitude);
            assertThat(result).hasSize(2);
            assertThat(result.get(0).gymId()).isEqualTo(firstId);
            assertThat(result.get(1).gymId()).isEqualTo(secondId);
        }

        @DisplayName("keyword가 주어졌을 때, 해당 키워드로 클라이밍장 목록을 필터링한다")
        void getGymList_whenKeywordProvided() {
            // given
            // 특정 키워드가 주어진 상황

            // when
            // 해당 키워드를 포함하는 클라이밍장 목록을 조회

            // then
            // 키워드에 해당하는 클라이밍장 목록이 반환된다
        }

    }
}
