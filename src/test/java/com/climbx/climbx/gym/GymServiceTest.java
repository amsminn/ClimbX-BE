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
import org.junit.jupiter.params.provider.ValueSource;
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
        @DisplayName("전체 클라이밍장 목록을 반환한다")
        void getGymList() {
            // given
            GymEntity gymEntity1 = GymFixture.createGymEntity(1L, "Gym1");
            GymEntity gymEntity2 = GymFixture.createGymEntity(2L, "Gym2");

            given(gymRepository.findAll())
                .willReturn(List.of(gymEntity1, gymEntity2));

            // when
            List<GymInfoResponseDto> gymList = gymService.getGymList();

            // then
            then(gymRepository).should(times(1)).findAll();
            assertThat(gymList.size()).isEqualTo(2);
            assertThat(gymList.get(0)).isEqualTo(GymFixture.createGymInfoResponseDto(1L, "Gym1"));
            assertThat(gymList.get(1)).isEqualTo(GymFixture.createGymInfoResponseDto(2L, "Gym2"));
        }


        @DisplayName("전달받은 위치 정보가 유효할 때, 현재 위치와 가까운 순으로 클라이밍장 목록을 반환한다")
        @ParameterizedTest
        @ValueSource(strings = {"37.5665,126.978", "35.1796,129.0756"})
        void getGymList_whenValidLocation() {
            // given
            // 유효한 위치 정보가 주어진 상황

            // when
            // 해당 위치 정보를 기반으로 클라이밍장 목록을 조회

            // then
            // 현재 위치와 가까운 순으로 정렬된 클라이밍장 목록이 반환된다
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
