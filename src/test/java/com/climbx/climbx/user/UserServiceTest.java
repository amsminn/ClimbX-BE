package com.climbx.climbx.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.climbx.climbx.fixture.ProblemFixture;
import com.climbx.climbx.fixture.UserFixture;
import com.climbx.climbx.problem.dto.ProblemResponseDto;
import com.climbx.climbx.problem.entity.ProblemEntity;
import com.climbx.climbx.problem.repository.ProblemRepository;
import com.climbx.climbx.submission.repository.SubmissionRepository;
import com.climbx.climbx.common.enums.UserHistoryCriteriaType;
import com.climbx.climbx.user.dto.DailyHistoryResponseDto;
import com.climbx.climbx.user.dto.UserProfileModifyRequestDto;
import com.climbx.climbx.user.dto.UserProfileResponseDto;
import com.climbx.climbx.user.entity.UserAccountEntity;
import com.climbx.climbx.user.entity.UserStatEntity;
import com.climbx.climbx.user.exception.DuplicateNicknameException;
import com.climbx.climbx.user.exception.NicknameMismatchException;
import com.climbx.climbx.user.exception.UserNotFoundException;
import com.climbx.climbx.user.exception.UserStatNotFoundException;
import com.climbx.climbx.user.repository.UserAccountRepository;
import com.climbx.climbx.user.repository.UserRankingHistoryRepository;
import com.climbx.climbx.user.repository.UserStatRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private UserStatRepository userStatRepository;

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private UserRankingHistoryRepository userRankingHistoryRepository;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("사용자 ID로 프로필 조회")
    class GetUserById {

        @Test
        @DisplayName("사용자 ID로 프로필을 정상 조회")
        void getUserById_Success() {
            // given
            Long userId = 1L;
            Long ratingRank = 10L;

            UserAccountEntity userAccountEntity = UserFixture.createUserAccountEntity(userId);
            UserStatEntity userStatEntity = UserFixture.createUserStatEntity(userId);

            given(userAccountRepository.findByUserId(userId))
                .willReturn(Optional.of(userAccountEntity));
            given(userStatRepository.findByUserId(userId))
                .willReturn(Optional.of(userStatEntity));
            given(userStatRepository.findRatingRank(UserFixture.DEFAULT_RATING))
                .willReturn(ratingRank);

            // when
            UserProfileResponseDto result = userService.getUserById(userId);

            // then
            UserProfileResponseDto expected = UserFixture.createUserProfileResponseDto(
                UserFixture.DEFAULT_NICKNAME, ratingRank);
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 조회")
        void getUserById_UserNotFound() {
            // given
            Long userId = 999L;
            given(userAccountRepository.findByUserId(userId))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with id: " + userId);
        }

        @Test
        @DisplayName("사용자는 존재하지만 통계 정보가 없음")
        void getUserById_UserStatNotFound() {
            // given
            Long userId = 1L;
            UserAccountEntity userAccountEntity = UserFixture.createUserAccountEntity(userId);

            given(userAccountRepository.findByUserId(userId))
                .willReturn(Optional.of(userAccountEntity));
            given(userStatRepository.findByUserId(userId))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(UserStatNotFoundException.class)
                .hasMessage("User stats not found for user: " + userId);
        }
    }

    @Nested
    @DisplayName("사용자 닉네임으로 프로필 조회")
    class GetUserByNickname {

        @Test
        @DisplayName("닉네임으로 사용자 프로필을 정상 조회")
        void getUserByNickname_Success() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            Long ratingRank = 10L;

            UserAccountEntity userAccountEntity = UserFixture.createUserAccountEntity(userId, nickname);
            UserStatEntity userStatEntity = UserFixture.createUserStatEntity(userId);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccountEntity));
            given(userStatRepository.findByUserId(userId))
                .willReturn(Optional.of(userStatEntity));
            given(userStatRepository.findRatingRank(UserFixture.DEFAULT_RATING))
                .willReturn(ratingRank);

            // when
            UserProfileResponseDto result = userService.getUserByNickname(nickname);

            // then
            UserProfileResponseDto expected = UserFixture.createUserProfileResponseDto(nickname, ratingRank);
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 닉네임으로 조회")
        void getUserByNickname_UserNotFound() {
            // given
            String nickname = "nonexistentUser";
            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getUserByNickname(nickname))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with newNickname: " + nickname);
        }

        @Test
        @DisplayName("사용자는 존재하지만 통계 정보가 없음")
        void getUserByNickname_UserStatNotFound() {
            // given
            String nickname = "testUser";
            Long userId = 1L;

            UserAccountEntity userAccountEntity = UserFixture.createUserAccountEntity(userId, nickname);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccountEntity));
            given(userStatRepository.findByUserId(userId))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getUserByNickname(nickname))
                .isInstanceOf(UserStatNotFoundException.class)
                .hasMessage("User stats not found for user: " + userId);
        }

        @Test
        @DisplayName("닉네임이 null인 사용자 조회")
        void getUserByNickname_NullNickname() {
            // given
            given(userAccountRepository.findByNickname(null))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getUserByNickname(null))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with newNickname: null");
        }

        @Test
        @DisplayName("빈 문자열 닉네임으로 사용자 조회")
        void getUserByNickname_EmptyNickname() {
            // given
            String emptyNickname = "";
            given(userAccountRepository.findByNickname(emptyNickname))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getUserByNickname(emptyNickname))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with newNickname: ");
        }
    }

    @Nested
    @DisplayName("사용자 프로필 수정")
    class ModifyUserProfile {

        @Test
        @DisplayName("사용자 프로필 정상 수정")
        void modifyUserProfile_Success() {
            // given
            Long userId = 1L;
            String currentNickname = "oldNickname";
            String newNickname = "newNickname";
            String newStatusMessage = "New status";
            String newProfileImageUrl = "new.jpg";
            Long rating = 1200L;
            Long ratingRank = 20L;

            UserProfileModifyRequestDto requestDto = new UserProfileModifyRequestDto(
                newNickname, newStatusMessage, newProfileImageUrl);

            UserAccountEntity userAccountEntity = UserFixture.createUserAccountEntity(
                userId, currentNickname, "Old status", "old.jpg");
            UserStatEntity userStatEntity = UserFixture.createUserStatEntity(
                userId, rating, 3L, 10L, 15L, 2L);

            given(userAccountRepository.findByUserId(userId))
                .willReturn(Optional.of(userAccountEntity));
            given(userAccountRepository.existsByNickname(newNickname))
                .willReturn(false);
            given(userStatRepository.findByUserId(userId))
                .willReturn(Optional.of(userStatEntity));
            given(userStatRepository.findRatingRank(rating))
                .willReturn(ratingRank);

            // when
            UserProfileResponseDto result = userService.modifyUserProfile(userId, currentNickname, requestDto);

            // then
            UserProfileResponseDto expected = UserFixture.createUserProfileResponseDto(
                newNickname, newStatusMessage, newProfileImageUrl, ratingRank, rating, 3L, 10L, 15L, 2L);
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 수정 시도")
        void modifyUserProfile_UserNotFound() {
            // given
            Long userId = 999L;
            String currentNickname = "oldNickname";
            UserProfileModifyRequestDto requestDto = new UserProfileModifyRequestDto(
                "newNickname", "New status", "new.jpg");

            given(userAccountRepository.findByUserId(userId))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.modifyUserProfile(userId, currentNickname, requestDto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with id: " + userId);

            then(userAccountRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("현재 닉네임과 요청 닉네임이 일치하지 않음")
        void modifyUserProfile_NicknameMismatch() {
            // given
            Long userId = 1L;
            String currentNickname = "oldNickname";
            String actualNickname = "actualNickname";

            UserProfileModifyRequestDto requestDto = new UserProfileModifyRequestDto(
                "newNickname", "New status", "new.jpg");

            UserAccountEntity userAccountEntity = UserFixture.createUserAccountEntity(userId, actualNickname);

            given(userAccountRepository.findByUserId(userId))
                .willReturn(Optional.of(userAccountEntity));

            // when & then
            assertThatThrownBy(() -> userService.modifyUserProfile(userId, currentNickname, requestDto))
                .isInstanceOf(NicknameMismatchException.class);

            then(userAccountRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("중복된 닉네임으로 수정 시도")
        void modifyUserProfile_DuplicateNickname() {
            // given
            Long userId = 1L;
            String currentNickname = "oldNickname";
            String duplicateNickname = "existingNickname";

            UserProfileModifyRequestDto requestDto = new UserProfileModifyRequestDto(
                duplicateNickname, "New status", "new.jpg");

            UserAccountEntity userAccountEntity = UserFixture.createUserAccountEntity(userId, currentNickname);

            given(userAccountRepository.findByUserId(userId))
                .willReturn(Optional.of(userAccountEntity));
            given(userAccountRepository.existsByNickname(duplicateNickname))
                .willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.modifyUserProfile(userId, currentNickname, requestDto))
                .isInstanceOf(DuplicateNicknameException.class)
                .hasMessage("Nickname already in use: " + duplicateNickname);

            then(userAccountRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("같은 닉네임으로 수정하는 경우 중복 체크 안함")
        void modifyUserProfile_SameNickname_NoDuplicateCheck() {
            // given
            Long userId = 1L;
            String currentNickname = "sameNickname";

            UserProfileModifyRequestDto requestDto = new UserProfileModifyRequestDto(
                currentNickname, "New status", "new.jpg");

            UserAccountEntity userAccountEntity = UserFixture.createUserAccountEntity(
                userId, currentNickname, "Old status", "old.jpg");
            UserStatEntity userStatEntity = UserFixture.createUserStatEntity(userId, 1000L);

            given(userAccountRepository.findByUserId(userId))
                .willReturn(Optional.of(userAccountEntity));
            given(userStatRepository.findByUserId(userId))
                .willReturn(Optional.of(userStatEntity));
            given(userStatRepository.findRatingRank(1000L))
                .willReturn(50L);

            // when
            UserProfileResponseDto result = userService.modifyUserProfile(userId, currentNickname, requestDto);

            // then
            assertThat(result).isNotNull();
            then(userAccountRepository).should(never()).existsByNickname(any());
        }

        @Test
        @DisplayName("프로필 수정 후 사용자 통계 조회 실패")
        void modifyUserProfile_UserStatNotFoundAfterUpdate() {
            // given
            Long userId = 1L;
            String currentNickname = "oldNickname";
            String newNickname = "newNickname";

            UserProfileModifyRequestDto requestDto = new UserProfileModifyRequestDto(
                newNickname, "New status", "new.jpg");

            UserAccountEntity userAccountEntity = UserFixture.createUserAccountEntity(userId, currentNickname);

            given(userAccountRepository.findByUserId(userId))
                .willReturn(Optional.of(userAccountEntity));
            given(userAccountRepository.existsByNickname(newNickname))
                .willReturn(false);
            given(userStatRepository.findByUserId(userId))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.modifyUserProfile(userId, currentNickname, requestDto))
                .isInstanceOf(UserStatNotFoundException.class)
                .hasMessage("User stats not found for user: " + userId);
        }
    }

    @Nested
    @DisplayName("사용자 상위 문제 조회")
    class GetUserTopProblems {

        @Test
        @DisplayName("사용자의 상위 문제를 정상 조회")
        void getUserTopProblems_Success() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            Integer limit = 5;

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId, nickname);
            
            ProblemEntity problem1 = ProblemFixture.createProblemEntity(1L, "Hard Problem", 1800L);
            ProblemEntity problem2 = ProblemFixture.createProblemEntity(2L, "Medium Problem", 1500L);
            ProblemEntity problem3 = ProblemFixture.createProblemEntity(3L, "Easy Problem", 1200L);
            
            List<ProblemEntity> problemEntities = List.of(problem1, problem2, problem3);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(submissionRepository.getUserSubmissionProblems(eq(userId), any(Pageable.class)))
                .willReturn(problemEntities);

            // when
            List<ProblemResponseDto> result = userService.getUserTopProblems(nickname, limit);

            // then
            List<ProblemResponseDto> expected = List.of(
                ProblemFixture.createProblemResponseDto(1L),
                ProblemFixture.createProblemResponseDto(2L),
                ProblemFixture.createProblemResponseDto(3L)
            );
            assertThat(result).isEqualTo(expected);

            then(submissionRepository).should().getUserSubmissionProblems(eq(userId), any(Pageable.class));
        }

        @Test
        @DisplayName("존재하지 않는 닉네임으로 상위 문제 조회")
        void getUserTopProblems_UserNotFound() {
            // given
            String nickname = "nonexistentUser";
            Integer limit = 5;

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getUserTopProblems(nickname, limit))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with newNickname: " + nickname);

            then(submissionRepository).should(never()).getUserSubmissionProblems(any(), any());
        }

        @Test
        @DisplayName("사용자에게 문제 제출 기록이 없는 경우")
        void getUserTopProblems_NoSubmissions() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            Integer limit = 5;

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId, nickname);
            List<ProblemEntity> emptyProblems = List.of();

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(submissionRepository.getUserSubmissionProblems(eq(userId), any(Pageable.class)))
                .willReturn(emptyProblems);

            // when
            List<ProblemResponseDto> result = userService.getUserTopProblems(nickname, limit);

            // then
            assertThat(result).isEmpty();
            then(submissionRepository).should().getUserSubmissionProblems(eq(userId), any(Pageable.class));
        }

        @Test
        @DisplayName("limit이 0인 경우 예외 발생")
        void getUserTopProblems_ZeroLimit() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            Integer limit = 0;

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId, nickname);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));

            // when & then
            assertThatThrownBy(() -> userService.getUserTopProblems(nickname, limit))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Page size must not be less than one");
        }

        @Test
        @DisplayName("요청한 limit보다 적은 문제가 있는 경우")
        void getUserTopProblems_LessProblemsThanlimit() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            Integer limit = 10;

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId, nickname);
            
            ProblemEntity problem1 = ProblemFixture.createProblemEntity(1L, "Problem 1", 1600L);
            ProblemEntity problem2 = ProblemFixture.createProblemEntity(2L, "Problem 2", 1400L);
            
            List<ProblemEntity> problemEntities = List.of(problem1, problem2);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(submissionRepository.getUserSubmissionProblems(eq(userId), any(Pageable.class)))
                .willReturn(problemEntities);

            // when
            List<ProblemResponseDto> result = userService.getUserTopProblems(nickname, limit);

            // then
            List<ProblemResponseDto> expected = List.of(
                ProblemFixture.createProblemResponseDto(1L),
                ProblemFixture.createProblemResponseDto(2L)
            );
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("사용자 스트릭 조회")
    class GetUserStreak {

        @Test
        @DisplayName("사용자의 일별 해결 문제 수를 정상 조회")
        void getUserStreak_Success() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            LocalDate from = LocalDate.of(2024, 1, 1);
            LocalDate to = LocalDate.of(2024, 1, 3);

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId, nickname);

            Object[] result1 = {LocalDate.of(2024, 1, 1), 3L};
            Object[] result2 = {LocalDate.of(2024, 1, 2), 5L};
            Object[] result3 = {LocalDate.of(2024, 1, 3), 2L};
            List<Object[]> queryResults = List.<Object[]>of(result1, result2, result3);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(submissionRepository.getUserDateSolvedCount(userId, from, to))
                .willReturn(queryResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserStreak(nickname, from, to);

            // then
            List<DailyHistoryResponseDto> expected = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 1), 3L),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 2), 5L),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 3), 2L)
            );
            assertThat(result).isEqualTo(expected);

            then(submissionRepository).should().getUserDateSolvedCount(userId, from, to);
        }

        @Test
        @DisplayName("존재하지 않는 닉네임으로 스트릭 조회")
        void getUserStreak_UserNotFound() {
            // given
            String nickname = "nonexistentUser";
            LocalDate from = LocalDate.of(2024, 1, 1);
            LocalDate to = LocalDate.of(2024, 1, 31);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getUserStreak(nickname, from, to))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with newNickname: " + nickname);

            then(submissionRepository).should(never()).getUserDateSolvedCount(any(), any(), any());
        }

        @Test
        @DisplayName("해당 기간에 해결한 문제가 없는 경우")
        void getUserStreak_NoSolvedProblems() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            LocalDate from = LocalDate.of(2024, 1, 1);
            LocalDate to = LocalDate.of(2024, 1, 31);

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId, nickname);
            List<Object[]> emptyResults = List.<Object[]>of();

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(submissionRepository.getUserDateSolvedCount(userId, from, to))
                .willReturn(emptyResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserStreak(nickname, from, to);

            // then
            assertThat(result).isEmpty();
            then(submissionRepository).should().getUserDateSolvedCount(userId, from, to);
        }

        @Test
        @DisplayName("하루만 조회하는 경우")
        void getUserStreak_SingleDay() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            LocalDate singleDate = LocalDate.of(2024, 1, 15);

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId, nickname);

            Object[] result1 = {LocalDate.of(2024, 1, 15), 7L};
            List<Object[]> queryResults = List.<Object[]>of(result1);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(submissionRepository.getUserDateSolvedCount(userId, singleDate, singleDate))
                .willReturn(queryResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserStreak(nickname, singleDate, singleDate);

            // then
            List<DailyHistoryResponseDto> expected = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 15), 7L)
            );
            assertThat(result).isEqualTo(expected);

            then(submissionRepository).should().getUserDateSolvedCount(userId, singleDate, singleDate);
        }

        @Test
        @DisplayName("날짜 순서가 잘못된 경우 (from > to)")
        void getUserStreak_InvalidDateRange() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            LocalDate from = LocalDate.of(2024, 1, 31);
            LocalDate to = LocalDate.of(2024, 1, 1);

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId, nickname);
            List<Object[]> emptyResults = List.<Object[]>of();

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(submissionRepository.getUserDateSolvedCount(userId, from, to))
                .willReturn(emptyResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserStreak(nickname, from, to);

            // then
            assertThat(result).isEmpty();
            then(submissionRepository).should().getUserDateSolvedCount(userId, from, to);
        }

        @Test
        @DisplayName("연속되지 않은 날짜의 데이터 조회")
        void getUserStreak_NonConsecutiveDates() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            LocalDate from = LocalDate.of(2024, 1, 1);
            LocalDate to = LocalDate.of(2024, 1, 10);

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId, nickname);

            // 1일, 5일, 9일에만 문제를 해결
            Object[] result1 = {LocalDate.of(2024, 1, 1), 2L};
            Object[] result2 = {LocalDate.of(2024, 1, 5), 4L};
            Object[] result3 = {LocalDate.of(2024, 1, 9), 1L};
            List<Object[]> queryResults = List.<Object[]>of(result1, result2, result3);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(submissionRepository.getUserDateSolvedCount(userId, from, to))
                .willReturn(queryResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserStreak(nickname, from, to);

            // then
            List<DailyHistoryResponseDto> expected = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 1), 2L),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 5), 4L),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 9), 1L)
            );
            assertThat(result).isEqualTo(expected);

            then(submissionRepository).should().getUserDateSolvedCount(userId, from, to);
        }

        @Test
        @DisplayName("null 파라미터로 조회하는 경우")
        void getUserStreak_WithNullParameters() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            LocalDate from = null;
            LocalDate to = null;

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId, nickname);

            Object[] result1 = {LocalDate.of(2024, 1, 1), 1L};
            Object[] result2 = {LocalDate.of(2024, 1, 2), 3L};
            List<Object[]> queryResults = List.<Object[]>of(result1, result2);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(submissionRepository.getUserDateSolvedCount(userId, from, to))
                .willReturn(queryResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserStreak(nickname, from, to);

            // then
            List<DailyHistoryResponseDto> expected = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 1), 1L),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 2), 3L)
            );
            assertThat(result).isEqualTo(expected);

            then(submissionRepository).should().getUserDateSolvedCount(userId, null, null);
        }

        @Test
        @DisplayName("from만 null인 경우")
        void getUserStreak_WithFromNull() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            LocalDate from = null;
            LocalDate to = LocalDate.of(2024, 1, 31);

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId, nickname);

            Object[] result1 = {LocalDate.of(2024, 1, 30), 2L};
            Object[] result2 = {LocalDate.of(2024, 1, 31), 4L};
            List<Object[]> queryResults = List.<Object[]>of(result1, result2);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(submissionRepository.getUserDateSolvedCount(userId, from, to))
                .willReturn(queryResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserStreak(nickname, from, to);

            // then
            List<DailyHistoryResponseDto> expected = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 30), 2L),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 31), 4L)
            );
            assertThat(result).isEqualTo(expected);

            then(submissionRepository).should().getUserDateSolvedCount(userId, null, to);
        }

        @Test
        @DisplayName("to만 null인 경우")
        void getUserStreak_WithToNull() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            LocalDate from = LocalDate.of(2024, 1, 1);
            LocalDate to = null;

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId, nickname);

            Object[] result1 = {LocalDate.of(2024, 1, 1), 1L};
            Object[] result2 = {LocalDate.of(2024, 2, 1), 5L};
            List<Object[]> queryResults = List.<Object[]>of(result1, result2);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(submissionRepository.getUserDateSolvedCount(userId, from, to))
                .willReturn(queryResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserStreak(nickname, from, to);

            // then
            List<DailyHistoryResponseDto> expected = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 1), 1L),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 2, 1), 5L)
            );
            assertThat(result).isEqualTo(expected);

            then(submissionRepository).should().getUserDateSolvedCount(userId, from, null);
        }
    }

    @Nested
    @DisplayName("사용자 일별 히스토리 조회")
    class GetUserDailyHistory {

        @Test
        @DisplayName("사용자의 레이팅 히스토리를 정상 조회")
        void getUserDailyHistory_Success() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            UserHistoryCriteriaType criteria = UserHistoryCriteriaType.RATING;
            LocalDate from = LocalDate.of(2024, 1, 1);
            LocalDate to = LocalDate.of(2024, 1, 3);

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId, nickname);

            Object[] result1 = {LocalDate.of(2024, 1, 1), 1200L};
            Object[] result2 = {LocalDate.of(2024, 1, 2), 1250L};
            Object[] result3 = {LocalDate.of(2024, 1, 3), 1300L};
            List<Object[]> queryResults = List.<Object[]>of(result1, result2, result3);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(userRankingHistoryRepository.getUserDailyHistory(userId, criteria, from, to))
                .willReturn(queryResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserDailyHistory(nickname, criteria, from, to);

            // then
            List<DailyHistoryResponseDto> expected = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 1), 1200L),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 2), 1250L),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 3), 1300L)
            );
            assertThat(result).isEqualTo(expected);

            then(userRankingHistoryRepository).should().getUserDailyHistory(userId, criteria, from, to);
        }

        @Test
        @DisplayName("존재하지 않는 닉네임으로 히스토리 조회")
        void getUserDailyHistory_UserNotFound() {
            // given
            String nickname = "nonexistentUser";
            UserHistoryCriteriaType criteria = UserHistoryCriteriaType.RANKING;
            LocalDate from = LocalDate.of(2024, 1, 1);
            LocalDate to = LocalDate.of(2024, 1, 31);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getUserDailyHistory(nickname, criteria, from, to))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with newNickname: " + nickname);

            then(userRankingHistoryRepository).should(never()).getUserDailyHistory(any(), any(), any(), any());
        }

        @Test
        @DisplayName("해당 기간에 히스토리가 없는 경우")
        void getUserDailyHistory_NoHistory() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            UserHistoryCriteriaType criteria = UserHistoryCriteriaType.SOLVED_COUNT;
            LocalDate from = LocalDate.of(2024, 1, 1);
            LocalDate to = LocalDate.of(2024, 1, 31);

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId, nickname);
            List<Object[]> emptyResults = List.<Object[]>of();

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(userRankingHistoryRepository.getUserDailyHistory(userId, criteria, from, to))
                .willReturn(emptyResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserDailyHistory(nickname, criteria, from, to);

            // then
            assertThat(result).isEmpty();
            then(userRankingHistoryRepository).should().getUserDailyHistory(userId, criteria, from, to);
        }

        @Test
        @DisplayName("다양한 criteria 타입으로 조회")
        void getUserDailyHistory_DifferentCriteria() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            UserHistoryCriteriaType criteria = UserHistoryCriteriaType.RANKING;
            LocalDate from = LocalDate.of(2024, 1, 1);
            LocalDate to = LocalDate.of(2024, 1, 2);

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId, nickname);

            Object[] result1 = {LocalDate.of(2024, 1, 1), 100L};
            Object[] result2 = {LocalDate.of(2024, 1, 2), 95L};
            List<Object[]> queryResults = List.<Object[]>of(result1, result2);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(userRankingHistoryRepository.getUserDailyHistory(userId, criteria, from, to))
                .willReturn(queryResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserDailyHistory(nickname, criteria, from, to);

            // then
            List<DailyHistoryResponseDto> expected = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 1), 100L),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 2), 95L)
            );
            assertThat(result).isEqualTo(expected);

            then(userRankingHistoryRepository).should().getUserDailyHistory(userId, criteria, from, to);
        }

        @Test
        @DisplayName("null 파라미터로 조회하는 경우")
        void getUserDailyHistory_WithNullParameters() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            UserHistoryCriteriaType criteria = UserHistoryCriteriaType.RATING;
            LocalDate from = null;
            LocalDate to = null;

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId, nickname);

            Object[] result1 = {LocalDate.of(2024, 1, 1), 1000L};
            Object[] result2 = {LocalDate.of(2024, 1, 2), 1050L};
            List<Object[]> queryResults = List.<Object[]>of(result1, result2);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(userRankingHistoryRepository.getUserDailyHistory(userId, criteria, from, to))
                .willReturn(queryResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserDailyHistory(nickname, criteria, from, to);

            // then
            List<DailyHistoryResponseDto> expected = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 1), 1000L),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 2), 1050L)
            );
            assertThat(result).isEqualTo(expected);

            then(userRankingHistoryRepository).should().getUserDailyHistory(userId, criteria, null, null);
        }

        @Test
        @DisplayName("하루만 조회하는 경우")
        void getUserDailyHistory_SingleDay() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            UserHistoryCriteriaType criteria = UserHistoryCriteriaType.RATING;
            LocalDate singleDate = LocalDate.of(2024, 1, 15);

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId, nickname);

            Object[] result1 = {LocalDate.of(2024, 1, 15), 1400L};
            List<Object[]> queryResults = List.<Object[]>of(result1);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(userRankingHistoryRepository.getUserDailyHistory(userId, criteria, singleDate, singleDate))
                .willReturn(queryResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserDailyHistory(nickname, criteria, singleDate, singleDate);

            // then
            List<DailyHistoryResponseDto> expected = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 15), 1400L)
            );
            assertThat(result).isEqualTo(expected);

            then(userRankingHistoryRepository).should().getUserDailyHistory(userId, criteria, singleDate, singleDate);
        }

        @Test
        @DisplayName("연속되지 않은 날짜의 히스토리 데이터 조회")
        void getUserDailyHistory_NonConsecutiveDates() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            UserHistoryCriteriaType criteria = UserHistoryCriteriaType.SOLVED_COUNT;
            LocalDate from = LocalDate.of(2024, 1, 1);
            LocalDate to = LocalDate.of(2024, 1, 10);

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId, nickname);

            // 1일, 5일, 9일에만 히스토리가 있음
            Object[] result1 = {LocalDate.of(2024, 1, 1), 10L};
            Object[] result2 = {LocalDate.of(2024, 1, 5), 15L};
            Object[] result3 = {LocalDate.of(2024, 1, 9), 20L};
            List<Object[]> queryResults = List.<Object[]>of(result1, result2, result3);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(userRankingHistoryRepository.getUserDailyHistory(userId, criteria, from, to))
                .willReturn(queryResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserDailyHistory(nickname, criteria, from, to);

            // then
            List<DailyHistoryResponseDto> expected = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 1), 10L),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 5), 15L),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 9), 20L)
            );
            assertThat(result).isEqualTo(expected);

            then(userRankingHistoryRepository).should().getUserDailyHistory(userId, criteria, from, to);
        }

        @Test
        @DisplayName("from만 null인 경우")
        void getUserDailyHistory_WithFromNull() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            UserHistoryCriteriaType criteria = UserHistoryCriteriaType.RANKING;
            LocalDate from = null;
            LocalDate to = LocalDate.of(2024, 1, 31);

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId, nickname);

            Object[] result1 = {LocalDate.of(2024, 1, 30), 50L};
            Object[] result2 = {LocalDate.of(2024, 1, 31), 45L};
            List<Object[]> queryResults = List.<Object[]>of(result1, result2);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(userRankingHistoryRepository.getUserDailyHistory(userId, criteria, from, to))
                .willReturn(queryResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserDailyHistory(nickname, criteria, from, to);

            // then
            List<DailyHistoryResponseDto> expected = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 30), 50L),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 31), 45L)
            );
            assertThat(result).isEqualTo(expected);

            then(userRankingHistoryRepository).should().getUserDailyHistory(userId, criteria, null, to);
        }

        @Test
        @DisplayName("to만 null인 경우")
        void getUserDailyHistory_WithToNull() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            UserHistoryCriteriaType criteria = UserHistoryCriteriaType.RATING;
            LocalDate from = LocalDate.of(2024, 1, 1);
            LocalDate to = null;

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId, nickname);

            Object[] result1 = {LocalDate.of(2024, 1, 1), 1000L};
            Object[] result2 = {LocalDate.of(2024, 2, 1), 1100L};
            List<Object[]> queryResults = List.<Object[]>of(result1, result2);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(userRankingHistoryRepository.getUserDailyHistory(userId, criteria, from, to))
                .willReturn(queryResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserDailyHistory(nickname, criteria, from, to);

            // then
            List<DailyHistoryResponseDto> expected = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 1), 1000L),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 2, 1), 1100L)
            );
            assertThat(result).isEqualTo(expected);

            then(userRankingHistoryRepository).should().getUserDailyHistory(userId, criteria, from, null);
        }
    }
}
