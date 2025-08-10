package com.climbx.climbx.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;

import com.climbx.climbx.common.enums.CriteriaType;
import com.climbx.climbx.common.enums.RoleType;
import com.climbx.climbx.common.enums.StatusType;
import com.climbx.climbx.common.service.S3Service;
import com.climbx.climbx.common.util.RatingUtil;
import com.climbx.climbx.fixture.ProblemFixture;
import com.climbx.climbx.fixture.UserFixture;
import com.climbx.climbx.gym.enums.GymTierType;
import com.climbx.climbx.problem.dto.ProblemInfoResponseDto;
import com.climbx.climbx.problem.enums.HoldColorType;
import com.climbx.climbx.submission.repository.SubmissionRepository;
import com.climbx.climbx.user.dto.DailyHistoryResponseDto;
import com.climbx.climbx.user.dto.UserProfileInfoModifyRequestDto;
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
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    private SubmissionRepository submissionRepository;

    @Mock
    private UserRankingHistoryRepository userRankingHistoryRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private RatingUtil ratingUtil;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("사용자 목록 조회 및 검색")
    class GetUsers {

        @BeforeEach
        void setUpGetUsersCommon() {
            lenient().when(ratingUtil.calculateCategoryRating(any(), any())).thenReturn(List.of());
            lenient().when(submissionRepository.getUserAcceptedSubmissionTagSummary(any(), any()))
                .thenReturn(List.of());
        }

        @Test
        @DisplayName("전체 사용자 목록을 정상 조회")
        void getUsers_Success_AllUsers() {
            // given
            String search = null;

            UserAccountEntity user1 = UserFixture.createUserAccountEntity(1L, "alice");
            UserAccountEntity user2 = UserFixture.createUserAccountEntity(2L, "bob");
            UserAccountEntity user3 = UserFixture.createUserAccountEntity(3L, "charlie");
            List<UserAccountEntity> userAccounts = List.of(user1, user2, user3);

            given(userAccountRepository.findByRole(RoleType.USER))
                .willReturn(userAccounts);
            UserFixture.stubUserStatAndRank(userStatRepository, 1L, 1200, 30);
            UserFixture.stubUserStatAndRank(userStatRepository, 2L, 1300, 20);
            UserFixture.stubUserStatAndRank(userStatRepository, 3L, 1400, 10);

            // 공통 lenient 스텁으로 대체됨

            // when
            List<UserProfileResponseDto> result = userService.getUsers(search);

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).nickname()).isEqualTo("alice");
            assertThat(result.get(1).nickname()).isEqualTo("bob");
            assertThat(result.get(2).nickname()).isEqualTo("charlie");

            then(userAccountRepository).should().findByRole(RoleType.USER);
            then(userAccountRepository).should(never())
                .findByRoleAndNicknameContaining(any(), any());
        }

        @ParameterizedTest(name = "공백 검색어('{0}')로 전체 사용자 목록 조회")
        @ValueSource(strings = {"", "   "})
        void getUsers_Success_BlankSearch(String search) {
            // given
            boolean isEmptyString = search.isEmpty();
            List<UserAccountEntity> userAccounts = isEmptyString
                ? List.of(
                UserFixture.createUserAccountEntity(1L, "test1"),
                UserFixture.createUserAccountEntity(2L, "test2")
            )
                : List.of(UserFixture.createUserAccountEntity(1L, "user1"));

            given(userAccountRepository.findByRole(RoleType.USER)).willReturn(userAccounts);

            for (UserAccountEntity ua : userAccounts) {
                UserFixture.stubUserStatAndRank(userStatRepository, ua.userId());
            }

            // when
            List<UserProfileResponseDto> result = userService.getUsers(search);

            // then
            assertThat(result).hasSize(isEmptyString ? 2 : 1);
            then(userAccountRepository).should().findByRole(RoleType.USER);
            then(userAccountRepository).should(never())
                .findByRoleAndNicknameContaining(any(), any());
        }

        @Test
        @DisplayName("닉네임 검색으로 특정 사용자들 조회")
        void getUsers_Success_WithSearch() {
            // given
            String search = "test";

            UserAccountEntity user1 = UserFixture.createUserAccountEntity(1L, "testuser1");
            UserAccountEntity user2 = UserFixture.createUserAccountEntity(2L, "testuser2");
            List<UserAccountEntity> userAccounts = List.of(user1, user2);

            given(userAccountRepository.findByRoleAndNicknameContaining(RoleType.USER, "test"))
                .willReturn(userAccounts);
            UserFixture.stubUserStatAndRank(userStatRepository, 1L, 1100, 40);
            UserFixture.stubUserStatAndRank(userStatRepository, 2L, 1600, 5);

            // 공통 lenient 스텁으로 대체됨

            // when
            List<UserProfileResponseDto> result = userService.getUsers(search);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).nickname()).isEqualTo("testuser1");
            assertThat(result.get(1).nickname()).isEqualTo("testuser2");

            then(userAccountRepository).should()
                .findByRoleAndNicknameContaining(RoleType.USER, "test");
            then(userAccountRepository).should(never()).findByRole(any());
        }

        @Test
        @DisplayName("검색 결과가 없는 경우")
        void getUsers_Success_NoResults() {
            // given
            String search = "nonexistent";
            List<UserAccountEntity> emptyUserAccounts = List.of();

            given(
                userAccountRepository.findByRoleAndNicknameContaining(RoleType.USER, "nonexistent"))
                .willReturn(emptyUserAccounts);

            // when
            List<UserProfileResponseDto> result = userService.getUsers(search);

            // then
            assertThat(result).isEmpty();

            then(userAccountRepository).should()
                .findByRoleAndNicknameContaining(RoleType.USER, "nonexistent");
            then(userStatRepository).should(never()).findByUserId(any());
        }

        @Test
        @DisplayName("검색어 앞뒤 공백 제거 후 검색")
        void getUsers_Success_TrimmedSearch() {
            // given
            String search = "  alice  ";

            UserAccountEntity user1 = UserFixture.createUserAccountEntity(1L, "alice123");
            List<UserAccountEntity> userAccounts = List.of(user1);

            given(userAccountRepository.findByRoleAndNicknameContaining(RoleType.USER, "alice"))
                .willReturn(userAccounts);
            UserFixture.stubUserStatAndRank(userStatRepository, 1L);

            // when
            List<UserProfileResponseDto> result = userService.getUsers(search);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).nickname()).isEqualTo("alice123");

            then(userAccountRepository).should()
                .findByRoleAndNicknameContaining(RoleType.USER, "alice");
        }

        @Test
        @DisplayName("사용자는 있지만 통계 정보가 없는 경우")
        void getUsers_UserStatNotFound() {
            // given
            String search = null;

            UserAccountEntity user1 = UserFixture.createUserAccountEntity(1L, "user1");
            List<UserAccountEntity> userAccounts = List.of(user1);

            given(userAccountRepository.findByRole(RoleType.USER))
                .willReturn(userAccounts);
            given(userStatRepository.findByUserId(1L))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getUsers(search))
                .isInstanceOf(UserStatNotFoundException.class);
        }

        @Test
        @DisplayName("다양한 레이팅을 가진 사용자들 조회")
        void getUsers_Success_DifferentRatings() {
            // given
            String search = "pro";

            List<UserAccountEntity> userAccounts = List.of(
                UserFixture.createUserAccountEntity(1L, "pro_player1"),
                UserFixture.createUserAccountEntity(2L, "pro_player2"),
                UserFixture.createUserAccountEntity(3L, "pro_player3")
            );

            given(userAccountRepository.findByRoleAndNicknameContaining(RoleType.USER, "pro"))
                .willReturn(userAccounts);
            UserFixture.stubStatsFor(
                userStatRepository,
                userAccounts,
                new int[]{2000, 1800, 2200},
                new int[]{3, 8, 1}
            );

            // when
            List<UserProfileResponseDto> result = userService.getUsers(search);

            // then
            assertThat(result).hasSize(3);

            UserFixture.assertUserProfile(result.get(0), "pro_player1", 2000, 3);
            UserFixture.assertUserProfile(result.get(1), "pro_player2", 1800, 8);
            UserFixture.assertUserProfile(result.get(2), "pro_player3", 2200, 1);
        }

        @Test
        @DisplayName("ADMIN 역할 사용자는 조회되지 않음")
        void getUsers_AdminNotIncluded() {
            // given
            String search = null;

            UserAccountEntity normalUser = UserFixture.createUserAccountEntity(2L, "user");
            List<UserAccountEntity> userAccounts = List.of(normalUser); // admin은 포함되지 않음

            given(userAccountRepository.findByRole(RoleType.USER))
                .willReturn(userAccounts);
            UserFixture.stubUserStatAndRank(userStatRepository, 2L);

            // when
            List<UserProfileResponseDto> result = userService.getUsers(search);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).nickname()).isEqualTo("user");

            then(userAccountRepository).should().findByRole(RoleType.USER);
            then(userAccountRepository).should(never()).findByRole(RoleType.ADMIN);
        }

        @Test
        @DisplayName("ADMIN 역할 사용자는 검색에서도 제외됨")
        void getUsers_AdminNotIncludedInSearch() {
            // given
            String search = "admin";

            UserAccountEntity normalUser = UserFixture.createUserAccountEntity(1L, "admin_user");
            List<UserAccountEntity> userAccounts = List.of(normalUser); // admin 역할이 아닌 사용자만 포함

            given(userAccountRepository.findByRoleAndNicknameContaining(RoleType.USER, "admin"))
                .willReturn(userAccounts);
            UserFixture.stubUserStatAndRank(userStatRepository, 1L);

            // when
            List<UserProfileResponseDto> result = userService.getUsers(search);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).nickname()).isEqualTo("admin_user");

            then(userAccountRepository).should()
                .findByRoleAndNicknameContaining(RoleType.USER, "admin");
            then(userAccountRepository).should(never()).findByRole(any());
        }
    }

    @Nested
    @DisplayName("사용자 ID로 프로필 조회")
    class GetUserById {

        @Test
        @DisplayName("사용자 ID로 프로필을 정상 조회")
        void getUserById_Success() {
            // given
            Long userId = 1L;
            Integer ratingRank = 10;

            UserAccountEntity userAccountEntity = UserFixture.createUserAccountEntity(userId);

            given(userAccountRepository.findByUserId(userId))
                .willReturn(Optional.of(userAccountEntity));
            UserFixture.stubUserStatAndRank(userStatRepository, userId, UserFixture.DEFAULT_RATING,
                ratingRank);

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
                .isInstanceOf(UserNotFoundException.class);
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
                .isInstanceOf(UserStatNotFoundException.class);
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
            Integer ratingRank = 10;            // given

            UserAccountEntity userAccountEntity = UserFixture.createUserAccountEntity(userId,
                nickname);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccountEntity));
            UserFixture.stubUserStatAndRank(userStatRepository, userId, UserFixture.DEFAULT_RATING,
                ratingRank);

            // when
            UserProfileResponseDto result = userService.getUserByNickname(nickname);

            // then
            UserProfileResponseDto expected = UserFixture.createUserProfileResponseDto(nickname,
                ratingRank);
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
                .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("사용자는 존재하지만 통계 정보가 없음")
        void getUserByNickname_UserStatNotFound() {
            // given
            String nickname = "testUser";
            Long userId = 1L;

            UserAccountEntity userAccountEntity = UserFixture.createUserAccountEntity(userId,
                nickname);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccountEntity));
            given(userStatRepository.findByUserId(userId))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getUserByNickname(nickname))
                .isInstanceOf(UserStatNotFoundException.class);
        }

        @Test
        @DisplayName("닉네임이 null인 사용자 조회")
        void getUserByNickname_NullNickname() {
            // given
            given(userAccountRepository.findByNickname(null))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getUserByNickname(null))
                .isInstanceOf(UserNotFoundException.class);
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
                .isInstanceOf(UserNotFoundException.class);
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
            // String newProfileImageUrl = "new.jpg";
            Integer rating = 1200;
            Integer ratingRank = 20;

            UserProfileInfoModifyRequestDto requestDto = UserProfileInfoModifyRequestDto.builder()
                .newNickname(newNickname)
                .newStatusMessage(newStatusMessage)
                .build();

            UserAccountEntity userAccountEntity = UserFixture.createUserAccountEntity(
                userId, currentNickname, "Old status", "old.jpg");

            given(userAccountRepository.findByUserId(userId))
                .willReturn(Optional.of(userAccountEntity));
            given(userAccountRepository.existsByNickname(newNickname))
                .willReturn(false);
            UserStatEntity userStatEntity = UserFixture.createUserStatEntity(
                userId, rating, 3, 10, 15, 2);
            UserFixture.stubUserStatAndRank(userStatRepository, userStatEntity, ratingRank);

            // when
            UserProfileResponseDto result = userService.modifyUserProfileInfo(userId,
                currentNickname,
                requestDto);

            // then
            UserProfileResponseDto expected = UserFixture.createUserProfileResponseDto(
                newNickname, newStatusMessage, "old.jpg", ratingRank, rating, 3, 10, 15,
                2);
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 수정 시도")
        void modifyUserProfile_UserNotFound() {
            // given
            Long userId = 999L;
            String currentNickname = "oldNickname";
            UserProfileInfoModifyRequestDto requestDto = UserProfileInfoModifyRequestDto.builder()
                .newNickname("newNickname")
                .newStatusMessage("New status")
                .build();

            given(userAccountRepository.findByUserId(userId))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                () -> userService.modifyUserProfileInfo(userId, currentNickname, requestDto))
                .isInstanceOf(UserNotFoundException.class);
            then(userAccountRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("현재 닉네임과 요청 닉네임이 일치하지 않음")
        void modifyUserProfile_NicknameMismatch() {
            // given
            Long userId = 1L;
            String currentNickname = "oldNickname";
            String actualNickname = "actualNickname";

            UserProfileInfoModifyRequestDto requestDto = UserProfileInfoModifyRequestDto.builder()
                .newNickname("newNickname")
                .newStatusMessage("New status")
                .build();

            UserAccountEntity userAccountEntity = UserFixture.createUserAccountEntity(userId,
                actualNickname);

            given(userAccountRepository.findByUserId(userId))
                .willReturn(Optional.of(userAccountEntity));

            // when & then
            assertThatThrownBy(
                () -> userService.modifyUserProfileInfo(userId, currentNickname, requestDto))
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

            UserProfileInfoModifyRequestDto requestDto = UserProfileInfoModifyRequestDto.builder()
                .newNickname(duplicateNickname)
                .newStatusMessage("New status")
                .build();

            UserAccountEntity userAccountEntity = UserFixture.createUserAccountEntity(userId,
                currentNickname);

            given(userAccountRepository.findByUserId(userId))
                .willReturn(Optional.of(userAccountEntity));
            given(userAccountRepository.existsByNickname(duplicateNickname))
                .willReturn(true);

            // when & then
            assertThatThrownBy(
                () -> userService.modifyUserProfileInfo(userId, currentNickname, requestDto))
                .isInstanceOf(DuplicateNicknameException.class);
            then(userAccountRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("같은 닉네임으로 수정하는 경우 중복 체크 안함")
        void modifyUserProfile_SameNickname_NoDuplicateCheck() {
            // given
            Long userId = 1L;
            String currentNickname = "sameNickname";

            UserProfileInfoModifyRequestDto requestDto = UserProfileInfoModifyRequestDto.builder()
                .newNickname(currentNickname)
                .newStatusMessage("New status")
                .build();

            UserAccountEntity userAccountEntity = UserFixture.createUserAccountEntity(
                userId, currentNickname, "Old status", "old.jpg");

            given(userAccountRepository.findByUserId(userId))
                .willReturn(Optional.of(userAccountEntity));
            UserFixture.stubUserStatAndRank(userStatRepository, userId, 1000, 50);

            // when
            UserProfileResponseDto result = userService.modifyUserProfileInfo(userId,
                currentNickname,
                requestDto);

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

            UserProfileInfoModifyRequestDto requestDto = UserProfileInfoModifyRequestDto.builder()
                .newNickname(newNickname)
                .newStatusMessage("New status")
                .build();

            UserAccountEntity userAccountEntity = UserFixture.createUserAccountEntity(userId,
                currentNickname);

            given(userAccountRepository.findByUserId(userId))
                .willReturn(Optional.of(userAccountEntity));
            given(userAccountRepository.existsByNickname(newNickname))
                .willReturn(false);
            given(userStatRepository.findByUserId(userId))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                () -> userService.modifyUserProfileInfo(userId, currentNickname, requestDto))
                .isInstanceOf(UserStatNotFoundException.class);
        }

    }

    @Nested
    @DisplayName("사용자 상위 문제 조회")
    class GetUserTopProblems {

        @BeforeEach
        void setUpTopProblemsCommon() {
            lenient().when(ratingUtil.calculateCategoryRating(any(), any())).thenReturn(List.of());
            lenient().when(submissionRepository.getUserAcceptedSubmissionTagSummary(any(), any()))
                .thenReturn(List.of());
        }

        @Test
        @DisplayName("사용자의 상위 문제를 정상 조회")
        void getUserTopProblems_Success() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            Integer limit = 5;
            UUID problemId1 = UUID.randomUUID();
            UUID problemId2 = UUID.randomUUID();
            UUID problemId3 = UUID.randomUUID();

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId,
                nickname);

            List<ProblemInfoResponseDto> problemInfoResponseDtoList = List.of(
                ProblemFixture.createProblemResponseDto(problemId1, 1L, "테스트 체육관1", 1L,
                    "메인 구역",
                    GymTierType.RED, HoldColorType.RED, 1800),
                ProblemFixture.createProblemResponseDto(problemId2, 2L, "테스트 체육관2", 2L,
                    "메인 구역",
                    GymTierType.BLUE, HoldColorType.BLUE, 1500),
                ProblemFixture.createProblemResponseDto(problemId3, 3L, "테스트 체육관3", 3L,
                    "메인 구역",
                    GymTierType.YELLOW, HoldColorType.YELLOW, 1200)
            );

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(
                submissionRepository.getUserTopProblems(eq(userId), eq(StatusType.ACCEPTED),
                    any(Pageable.class)))
                .willReturn(problemInfoResponseDtoList);

            // when
            List<ProblemInfoResponseDto> result = userService.getUserTopProblems(nickname,
                limit);

            // then
            assertThat(result).isEqualTo(problemInfoResponseDtoList);

            then(submissionRepository).should()
                .getUserTopProblems(eq(userId), eq(StatusType.ACCEPTED),
                    any(Pageable.class));
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
                .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("사용자에게 문제 제출 기록이 없는 경우")
        void getUserTopProblems_NoSubmissions() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            Integer limit = 5;

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId,
                nickname);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(
                submissionRepository.getUserTopProblems(eq(userId), eq(StatusType.ACCEPTED),
                    any(Pageable.class)))
                .willReturn(List.of());

            // when
            List<ProblemInfoResponseDto> result = userService.getUserTopProblems(nickname,
                limit);

            // then
            assertThat(result).isEmpty();
            then(submissionRepository).should()
                .getUserTopProblems(eq(userId), eq(StatusType.ACCEPTED),
                    any(Pageable.class));
        }

        @ParameterizedTest(name = "limit={0} 이면 예외 발생")
        @ValueSource(ints = {0, -1})
        void getUserTopProblems_InvalidLimit(int limit) {
            String nickname = "testUser";
            Long userId = 1L;
            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId, nickname);
            given(userAccountRepository.findByNickname(nickname)).willReturn(
                Optional.of(userAccount));
            assertThatThrownBy(() -> userService.getUserTopProblems(nickname, limit))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("요청한 limit보다 적은 문제가 있는 경우")
        void getUserTopProblems_LessProblemsThanlimit() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            Integer limit = 10;
            UUID problemId1 = UUID.randomUUID();
            UUID problemId2 = UUID.randomUUID();

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId,
                nickname);

            List<ProblemInfoResponseDto> problemInfoResponseDtoList = List.of(
                ProblemFixture.createProblemResponseDto(problemId1, 1L, "테스트 체육관1", 1L, "메인 구역",
                    GymTierType.RED, HoldColorType.RED, 1600),
                ProblemFixture.createProblemResponseDto(problemId2, 2L, "테스트 체육관2", 2L, "메인 구역",
                    GymTierType.BLUE, HoldColorType.BLUE, 1400)
            );

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(
                submissionRepository.getUserTopProblems(eq(userId), eq(StatusType.ACCEPTED),
                    any(Pageable.class)))
                .willReturn(problemInfoResponseDtoList);

            // when
            List<ProblemInfoResponseDto> result = userService.getUserTopProblems(nickname,
                limit);

            // then
            assertThat(result).isEqualTo(problemInfoResponseDtoList);
        }
    }

    @Nested
    @DisplayName("사용자 스트릭 조회")
    class GetUserStreak {

        @BeforeEach
        void setUpStreakCommon() {
            lenient().when(submissionRepository.getUserDateSolvedCount(any(), any(), any(), any()))
                .thenReturn(List.of());
        }

        @Test
        @DisplayName("사용자의 일별 해결 문제 수를 정상 조회")
        void getUserStreak_Success() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            LocalDate from = LocalDate.of(2024, 1, 1);
            LocalDate to = LocalDate.of(2024, 1, 3);

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId,
                nickname);

            List<DailyHistoryResponseDto> queryResults = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 1), 3),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 2), 5),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 3), 2)
            );

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(submissionRepository.getUserDateSolvedCount(userId, StatusType.ACCEPTED, from,
                to))
                .willReturn(queryResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserStreak(nickname, from,
                to);

            // then
            assertThat(result).isEqualTo(queryResults);

            then(submissionRepository).should()
                .getUserDateSolvedCount(userId, StatusType.ACCEPTED, from, to);
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
                .isInstanceOf(UserNotFoundException.class);

            then(submissionRepository).should(never())
                .getUserDateSolvedCount(any(), any(), any(), any());
        }

        @Test
        @DisplayName("해당 기간에 해결한 문제가 없는 경우")
        void getUserStreak_NoSolvedProblems() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            LocalDate from = LocalDate.of(2024, 1, 1);
            LocalDate to = LocalDate.of(2024, 1, 31);

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId,
                nickname);
            List<DailyHistoryResponseDto> emptyResults = List.of();

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(submissionRepository.getUserDateSolvedCount(userId, StatusType.ACCEPTED, from,
                to))
                .willReturn(emptyResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserStreak(nickname, from,
                to);

            // then
            assertThat(result).isEmpty();
            then(submissionRepository).should()
                .getUserDateSolvedCount(userId, StatusType.ACCEPTED, from, to);
        }

        @Test
        @DisplayName("하루만 조회하는 경우")
        void getUserStreak_SingleDay() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            LocalDate singleDate = LocalDate.of(2024, 1, 15);

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId,
                nickname);

            List<DailyHistoryResponseDto> queryResults = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 15), 7)
            );

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(submissionRepository.getUserDateSolvedCount(userId, StatusType.ACCEPTED,
                singleDate,
                singleDate))
                .willReturn(queryResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserStreak(nickname,
                singleDate,
                singleDate);

            // then
            assertThat(result).isEqualTo(queryResults);

            then(submissionRepository).should()
                .getUserDateSolvedCount(userId, StatusType.ACCEPTED, singleDate, singleDate);
        }

        @Test
        @DisplayName("날짜 순서가 잘못된 경우 (from > to)")
        void getUserStreak_InvalidDateRange() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            LocalDate from = LocalDate.of(2024, 1, 31);
            LocalDate to = LocalDate.of(2024, 1, 1);

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId,
                nickname);
            List<DailyHistoryResponseDto> emptyResults = List.of();

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(submissionRepository.getUserDateSolvedCount(userId, StatusType.ACCEPTED, from,
                to))
                .willReturn(emptyResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserStreak(nickname, from,
                to);

            // then
            assertThat(result).isEmpty();
            then(submissionRepository).should()
                .getUserDateSolvedCount(userId, StatusType.ACCEPTED, from, to);
        }

        @Test
        @DisplayName("연속되지 않은 날짜의 데이터 조회")
        void getUserStreak_NonConsecutiveDates() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            LocalDate from = LocalDate.of(2024, 1, 1);
            LocalDate to = LocalDate.of(2024, 1, 10);

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId,
                nickname);

            // 1일, 5일, 9일에만 문제를 해결
            List<DailyHistoryResponseDto> queryResults = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 1), 2),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 5), 4),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 9), 1)
            );

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(submissionRepository.getUserDateSolvedCount(userId, StatusType.ACCEPTED, from,
                to))
                .willReturn(queryResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserStreak(nickname, from,
                to);

            // then
            assertThat(result).isEqualTo(queryResults);

            then(submissionRepository).should()
                .getUserDateSolvedCount(userId, StatusType.ACCEPTED, from, to);
        }
    }

    @Nested
    @DisplayName("사용자 일별 히스토리 조회")
    class GetUserDailyHistory {

        @BeforeEach
        void setUpDailyHistoryCommon() {
            lenient().when(
                    userRankingHistoryRepository.getUserDailyHistory(any(), any(), any(), any()))
                .thenReturn(List.of());
        }

        @Test
        @DisplayName("사용자의 레이팅 히스토리를 정상 조회")
        void getUserDailyHistory_Success() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            CriteriaType criteria = CriteriaType.RATING;
            LocalDate from = LocalDate.of(2024, 1, 1);
            LocalDate to = LocalDate.of(2024, 1, 3);

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId,
                nickname);

            List<DailyHistoryResponseDto> queryResults = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 1), 1200),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 2), 1250),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 3), 1300)
            );

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(userRankingHistoryRepository.getUserDailyHistory(userId, criteria, from, to))
                .willReturn(queryResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserDailyHistory(nickname,
                criteria, from, to);

            // then
            assertThat(result).isEqualTo(queryResults);

            then(userRankingHistoryRepository).should()
                .getUserDailyHistory(userId, criteria, from, to);
        }

        @Test
        @DisplayName("존재하지 않는 닉네임으로 히스토리 조회")
        void getUserDailyHistory_UserNotFound() {
            // given
            String nickname = "nonexistentUser";
            CriteriaType criteria = CriteriaType.RATING;
            LocalDate from = LocalDate.of(2024, 1, 1);
            LocalDate to = LocalDate.of(2024, 1, 31);

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                () -> userService.getUserDailyHistory(nickname, criteria, from, to))
                .isInstanceOf(UserNotFoundException.class);

            then(userRankingHistoryRepository).should(never())
                .getUserDailyHistory(any(), any(), any(), any());
        }

        @Test
        @DisplayName("해당 기간에 히스토리가 없는 경우")
        void getUserDailyHistory_NoHistory() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            CriteriaType criteria = CriteriaType.SOLVED_COUNT;
            LocalDate from = LocalDate.of(2024, 1, 1);
            LocalDate to = LocalDate.of(2024, 1, 31);

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId,
                nickname);
            List<DailyHistoryResponseDto> emptyResults = List.of();

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(userRankingHistoryRepository.getUserDailyHistory(userId, criteria, from, to))
                .willReturn(emptyResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserDailyHistory(nickname,
                criteria, from, to);

            // then
            assertThat(result).isEmpty();
            then(userRankingHistoryRepository).should()
                .getUserDailyHistory(userId, criteria, from, to);
        }

        @Test
        @DisplayName("다양한 criteria 타입으로 조회")
        void getUserDailyHistory_DifferentCriteria() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            CriteriaType criteria = CriteriaType.SOLVED_COUNT;
            LocalDate from = LocalDate.of(2024, 1, 1);
            LocalDate to = LocalDate.of(2024, 1, 2);

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId,
                nickname);

            List<DailyHistoryResponseDto> queryResults = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 1), 100),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 2), 95)
            );

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(userRankingHistoryRepository.getUserDailyHistory(userId, criteria, from, to))
                .willReturn(queryResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserDailyHistory(nickname,
                criteria, from, to);

            // then
            assertThat(result).isEqualTo(queryResults);

            then(userRankingHistoryRepository).should()
                .getUserDailyHistory(userId, criteria, from, to);
        }

        @Test
        @DisplayName("null 파라미터로 조회하는 경우 (케이스1)")
        void getUserDailyHistory_WithNullParameters_case1() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            CriteriaType criteria = CriteriaType.RATING;
            LocalDate from = null;
            LocalDate to = null;

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId,
                nickname);

            List<DailyHistoryResponseDto> queryResults = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 1), 1000),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 2), 1050)
            );

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(userRankingHistoryRepository.getUserDailyHistory(userId, criteria, from, to))
                .willReturn(queryResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserDailyHistory(nickname,
                criteria, from, to);

            // then
            assertThat(result).isEqualTo(queryResults);

            then(userRankingHistoryRepository).should()
                .getUserDailyHistory(userId, criteria, null, null);
        }

        @Test
        @DisplayName("하루만 조회하는 경우")
        void getUserDailyHistory_SingleDay() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            CriteriaType criteria = CriteriaType.RATING;
            LocalDate singleDate = LocalDate.of(2024, 1, 15);

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId,
                nickname);

            List<DailyHistoryResponseDto> queryResults = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 15), 1400)
            );

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(userRankingHistoryRepository.getUserDailyHistory(userId, criteria, singleDate,
                singleDate))
                .willReturn(queryResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserDailyHistory(nickname,
                criteria, singleDate, singleDate);

            // then
            List<DailyHistoryResponseDto> expected = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 15), 1400)
            );
            assertThat(result).isEqualTo(expected);

            then(userRankingHistoryRepository).should()
                .getUserDailyHistory(userId, criteria, singleDate, singleDate);
        }

        @Test
        @DisplayName("연속되지 않은 날짜의 히스토리 데이터 조회")
        void getUserDailyHistory_NonConsecutiveDates() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            CriteriaType criteria = CriteriaType.SOLVED_COUNT;
            LocalDate from = LocalDate.of(2024, 1, 1);
            LocalDate to = LocalDate.of(2024, 1, 10);

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId,
                nickname);

            // 1일, 5일, 9일에만 히스토리가 있음
            List<DailyHistoryResponseDto> queryResults = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 1), 10),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 5), 15),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 9), 20)
            );

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccount));
            given(userRankingHistoryRepository.getUserDailyHistory(userId, criteria, from, to))
                .willReturn(queryResults);

            // when
            List<DailyHistoryResponseDto> result = userService.getUserDailyHistory(nickname,
                criteria, from, to);

            // then
            List<DailyHistoryResponseDto> expected = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 1), 10),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 5), 15),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 9), 20)
            );
            assertThat(result).isEqualTo(expected);

            then(userRankingHistoryRepository).should()
                .getUserDailyHistory(userId, criteria, from, to);
        }

        @Test
        @DisplayName("from만 null인 경우")
        void getUserDailyHistory_WithFromNull() {
            String nickname = "testUser";
            Long userId = 1L;
            CriteriaType criteria = CriteriaType.RATING;
            LocalDate from = null;
            LocalDate to = LocalDate.of(2024, 1, 31);

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId, nickname);

            List<DailyHistoryResponseDto> queryResults = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 30), 50),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 31), 45)
            );

            given(userAccountRepository.findByNickname(nickname)).willReturn(
                Optional.of(userAccount));
            given(userRankingHistoryRepository.getUserDailyHistory(userId, criteria, from,
                to)).willReturn(queryResults);

            List<DailyHistoryResponseDto> result = userService.getUserDailyHistory(nickname,
                criteria, from, to);
            assertThat(result).isEqualTo(queryResults);
            then(userRankingHistoryRepository).should()
                .getUserDailyHistory(userId, criteria, null, to);
        }

        @Test
        @DisplayName("to만 null인 경우 (케이스1)")
        void getUserDailyHistory_WithToNull_case1() {
            String nickname = "testUser";
            Long userId = 1L;
            CriteriaType criteria = CriteriaType.RATING;
            LocalDate from = LocalDate.of(2024, 1, 1);
            LocalDate to = null;

            UserAccountEntity userAccount = UserFixture.createUserAccountEntity(userId, nickname);

            List<DailyHistoryResponseDto> queryResults = List.of(
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 1, 1), 1000),
                UserFixture.createDailyHistoryResponseDto(LocalDate.of(2024, 2, 1), 1100)
            );

            given(userAccountRepository.findByNickname(nickname)).willReturn(
                Optional.of(userAccount));
            given(userRankingHistoryRepository.getUserDailyHistory(userId, criteria, from,
                to)).willReturn(queryResults);

            List<DailyHistoryResponseDto> result = userService.getUserDailyHistory(nickname,
                criteria, from, to);
            assertThat(result).isEqualTo(queryResults);
            then(userRankingHistoryRepository).should()
                .getUserDailyHistory(userId, criteria, from, null);
        }
    }
}