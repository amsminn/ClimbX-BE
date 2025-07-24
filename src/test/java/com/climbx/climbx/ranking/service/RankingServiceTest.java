package com.climbx.climbx.ranking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.climbx.climbx.fixture.UserFixture;
import com.climbx.climbx.ranking.dto.RankingResponseDto;
import com.climbx.climbx.ranking.exception.InvalidCriteriaException;
import com.climbx.climbx.ranking.repository.RankingRepository;
import com.climbx.climbx.user.entity.UserAccountEntity;
import com.climbx.climbx.user.entity.UserStatEntity;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("RankingService 테스트")
class RankingServiceTest {

    @Mock
    private RankingRepository rankingRepository;

    @InjectMocks
    private RankingService rankingService;

    @Nested
    @DisplayName("랭킹 조회 테스트")
    class GetRankingPageTest {

        @Test
        @DisplayName("레이팅 기준 내림차순 랭킹을 성공적으로 조회한다")
        void shouldGetRankingPageByRatingDesc() {
            // given
            String criteria = "rating";
            String order = "desc";
            Integer page = 0;
            Integer perPage = 10;

            UserAccountEntity userAccount1 = UserFixture.createUserAccountEntity(1L, "alice");
            UserAccountEntity userAccount2 = UserFixture.createUserAccountEntity(2L, "bob");

            UserStatEntity userStat1 = UserStatEntity.builder()
                .userId(1L)
                .userAccountEntity(userAccount1)
                .rating(1500)
                .currentStreak(10)
                .longestStreak(20)
                .solvedProblemsCount(50)
                .rivalCount(5)
                .build();

            UserStatEntity userStat2 = UserStatEntity.builder()
                .userId(2L)
                .userAccountEntity(userAccount2)
                .rating(1200)
                .currentStreak(5)
                .longestStreak(15)
                .solvedProblemsCount(30)
                .rivalCount(3)
                .build();

            List<UserStatEntity> userStats = List.of(userStat1, userStat2);
            Page<UserStatEntity> mockPage = new PageImpl<>(userStats, PageRequest.of(page, perPage),
                2);

            given(rankingRepository.findAllByUserRole(any(Pageable.class), anyString()))
                .willReturn(mockPage);

            // when
            RankingResponseDto result = rankingService.getRankingPage(criteria, order, page,
                perPage);

            // then
            assertThat(result.totalCount()).isEqualTo(2);
            assertThat(result.page()).isEqualTo(0);
            assertThat(result.perPage()).isEqualTo(10);
            assertThat(result.totalPage()).isEqualTo(1);
            assertThat(result.rankingList()).hasSize(2);

            assertThat(result.rankingList().get(0).nickname()).isEqualTo("alice");
            assertThat(result.rankingList().get(0).rating()).isEqualTo(1500);
            assertThat(result.rankingList().get(1).nickname()).isEqualTo("bob");
            assertThat(result.rankingList().get(1).rating()).isEqualTo(1200);

            then(rankingRepository).should().findAllByUserRole(any(Pageable.class), anyString());
        }

        @Test
        @DisplayName("연속 출석일 기준 오름차순 랭킹을 성공적으로 조회한다")
        void shouldGetRankingPageByStreakAsc() {
            // given
            String criteria = "streak";
            String order = "asc";
            Integer page = 0;
            Integer perPage = 10;

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(1L, "alice");
            UserStatEntity userStat = UserStatEntity.builder()
                .userId(1L)
                .userAccountEntity(userAccount)
                .rating(1500)
                .currentStreak(10)
                .longestStreak(20)
                .solvedProblemsCount(50)
                .rivalCount(5)
                .build();

            List<UserStatEntity> userStats = List.of(userStat);
            Page<UserStatEntity> mockPage = new PageImpl<>(userStats, PageRequest.of(page, perPage),
                1);

            given(rankingRepository.findAllByUserRole(any(Pageable.class), anyString()))
                .willReturn(mockPage);

            // when
            RankingResponseDto result = rankingService.getRankingPage(criteria, order, page,
                perPage);

            // then
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.rankingList()).hasSize(1);
            assertThat(result.rankingList().get(0).currentStreak()).isEqualTo(10);

            then(rankingRepository).should().findAllByUserRole(any(Pageable.class), anyString());
        }

        @Test
        @DisplayName("해결 문제 수 기준 랭킹을 성공적으로 조회한다")
        void shouldGetRankingPageBySolvedCount() {
            // given
            String criteria = "solvedProblemsCount";
            String order = "desc";
            Integer page = 0;
            Integer perPage = 10;

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(1L, "alice");
            UserStatEntity userStat = UserStatEntity.builder()
                .userId(1L)
                .userAccountEntity(userAccount)
                .rating(1500)
                .currentStreak(10)
                .longestStreak(20)
                .solvedProblemsCount(50)
                .rivalCount(5)
                .build();

            List<UserStatEntity> userStats = List.of(userStat);
            Page<UserStatEntity> mockPage = new PageImpl<>(userStats, PageRequest.of(page, perPage),
                1);

            given(rankingRepository.findAllByUserRole(any(Pageable.class), anyString()))
                .willReturn(mockPage);

            // when
            RankingResponseDto result = rankingService.getRankingPage(criteria, order, page,
                perPage);

            // then
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.rankingList()).hasSize(1);
            assertThat(result.rankingList().get(0).solvedCount()).isEqualTo(50);

            then(rankingRepository).should().findAllByUserRole(any(Pageable.class), anyString());
        }

        @Test
        @DisplayName("최장 연속 출석일 기준 랭킹을 성공적으로 조회한다")
        void shouldGetRankingPageByLongestStreak() {
            // given
            String criteria = "longestStreak";
            String order = "desc";
            Integer page = 0;
            Integer perPage = 10;

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(1L, "alice");
            UserStatEntity userStat = UserStatEntity.builder()
                .userId(1L)
                .userAccountEntity(userAccount)
                .rating(1500)
                .currentStreak(10)
                .longestStreak(20)
                .solvedProblemsCount(50)
                .rivalCount(5)
                .build();

            List<UserStatEntity> userStats = List.of(userStat);
            Page<UserStatEntity> mockPage = new PageImpl<>(userStats, PageRequest.of(page, perPage),
                1);

            given(rankingRepository.findAllByUserRole(any(Pageable.class), anyString()))
                .willReturn(mockPage);

            // when
            RankingResponseDto result = rankingService.getRankingPage(criteria, order, page,
                perPage);

            // then
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.rankingList()).hasSize(1);
            assertThat(result.rankingList().get(0).longestStreak()).isEqualTo(20);

            then(rankingRepository).should().findAllByUserRole(any(Pageable.class), anyString());
        }

        @Test
        @DisplayName("페이징 처리가 정상적으로 동작한다")
        void shouldHandlePagination() {
            // given
            String criteria = "rating";
            String order = "desc";
            Integer page = 1;
            Integer perPage = 5;

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(1L, "alice");
            UserStatEntity userStat = UserStatEntity.builder()
                .userId(1L)
                .userAccountEntity(userAccount)
                .rating(1500)
                .currentStreak(10)
                .longestStreak(20)
                .solvedProblemsCount(50)
                .rivalCount(5)
                .build();

            List<UserStatEntity> userStats = List.of(userStat);
            Page<UserStatEntity> mockPage = new PageImpl<>(userStats, PageRequest.of(page, perPage),
                10);

            given(rankingRepository.findAllByUserRole(any(Pageable.class), anyString()))
                .willReturn(mockPage);

            // when
            RankingResponseDto result = rankingService.getRankingPage(criteria, order, page,
                perPage);

            // then
            assertThat(result.totalCount()).isEqualTo(10);
            assertThat(result.page()).isEqualTo(1);
            assertThat(result.perPage()).isEqualTo(5);
            assertThat(result.totalPage()).isEqualTo(2);

            then(rankingRepository).should().findAllByUserRole(any(Pageable.class), anyString());
        }

        @Test
        @DisplayName("잘못된 order 파라미터일 때 기본값 DESC를 사용한다")
        void shouldUseDefaultOrderWhenInvalidOrder() {
            // given
            String criteria = "rating";
            String order = "invalid_order";
            Integer page = 0;
            Integer perPage = 10;

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(1L, "alice");
            UserStatEntity userStat = UserStatEntity.builder()
                .userId(1L)
                .userAccountEntity(userAccount)
                .rating(1500)
                .currentStreak(10)
                .longestStreak(20)
                .solvedProblemsCount(50)
                .rivalCount(5)
                .build();

            List<UserStatEntity> userStats = List.of(userStat);
            Page<UserStatEntity> mockPage = new PageImpl<>(userStats, PageRequest.of(page, perPage),
                1);

            given(rankingRepository.findAllByUserRole(any(Pageable.class), anyString()))
                .willReturn(mockPage);

            // when
            RankingResponseDto result = rankingService.getRankingPage(criteria, order, page,
                perPage);

            // then
            assertThat(result).isNotNull();
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.rankingList()).hasSize(1);

            then(rankingRepository).should().findAllByUserRole(any(Pageable.class), anyString());
        }

        @Test
        @DisplayName("잘못된 criteria 파라미터일 때 InvalidCriteriaException을 던진다")
        void shouldThrowExceptionWhenInvalidCriteria() {
            // given
            String criteria = "invalid_criteria";
            String order = "desc";
            Integer page = 0;
            Integer perPage = 10;

            // when & then
            assertThatThrownBy(() -> rankingService.getRankingPage(criteria, order, page, perPage))
                .isInstanceOf(InvalidCriteriaException.class);
        }

        @Test
        @DisplayName("빈 결과를 정상적으로 처리한다")
        void shouldHandleEmptyResult() {
            // given
            String criteria = "rating";
            String order = "desc";
            Integer page = 0;
            Integer perPage = 10;

            List<UserStatEntity> userStats = List.of();
            Page<UserStatEntity> mockPage = new PageImpl<>(userStats, PageRequest.of(page, perPage),
                0);

            given(rankingRepository.findAllByUserRole(any(Pageable.class), anyString()))
                .willReturn(mockPage);

            // when
            RankingResponseDto result = rankingService.getRankingPage(criteria, order, page,
                perPage);

            // then
            assertThat(result.totalCount()).isEqualTo(0);
            assertThat(result.page()).isEqualTo(0);
            assertThat(result.perPage()).isEqualTo(10);
            assertThat(result.totalPage()).isEqualTo(0);
            assertThat(result.rankingList()).isEmpty();

            then(rankingRepository).should().findAllByUserRole(any(Pageable.class), anyString());
        }
    }
} 