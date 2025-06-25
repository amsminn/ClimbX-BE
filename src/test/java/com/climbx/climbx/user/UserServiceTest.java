package com.climbx.climbx.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.climbx.climbx.user.dto.UserProfileModifyRequestDto;
import com.climbx.climbx.user.dto.UserProfileResponseDto;
import com.climbx.climbx.user.entity.UserAccountEntity;
import com.climbx.climbx.user.entity.UserStatEntity;
import com.climbx.climbx.user.exception.DuplicateNicknameException;
import com.climbx.climbx.user.exception.NicknameMismatchException;
import com.climbx.climbx.user.exception.UserNotFoundException;
import com.climbx.climbx.user.exception.UserStatNotFoundException;
import com.climbx.climbx.user.repository.UserAccountRepository;
import com.climbx.climbx.user.repository.UserStatRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private UserStatRepository userStatRepository;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("사용자 닉네임으로 프로필 조회")
    class GetUserByNickname {

        @Test
        @DisplayName("닉네임으로 사용자 프로필을 정상 조회")
        void getUserByNickname_Success() {
            // given
            String nickname = "testUser";
            Long userId = 1L;
            Long rating = 1500L;
            Long ratingRank = 10L;

            UserAccountEntity userAccountEntity = UserAccountEntity.builder()
                .userId(userId)
                .nickname(nickname)
                .statusMessage("Test status")
                .profileImageUrl("test.jpg")
                .build();

            UserStatEntity userStatEntity = UserStatEntity.builder()
                .userId(userId)
                .rating(rating)
                .currentStreak(5L)
                .longestStreak(15L)
                .solvedProblemsCount(25L)
                .rivalCount(3L)
                .build();

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccountEntity));
            given(userStatRepository.findByUserId(userId))
                .willReturn(Optional.of(userStatEntity));
            given(userStatRepository.findRatingRank(rating))
                .willReturn(ratingRank);

            // when
            UserProfileResponseDto result = userService.getUserByNickname(nickname);

            // then
            assertThat(result).isNotNull();
            assertThat(result.nickname()).isEqualTo(nickname);
            assertThat(result.statusMessage()).isEqualTo("Test status");
            assertThat(result.profileImageUrl()).isEqualTo("test.jpg");
            assertThat(result.ranking()).isEqualTo(ratingRank);
            assertThat(result.rating()).isEqualTo(rating);
            assertThat(result.currentStreak()).isEqualTo(5L);
            assertThat(result.longestStreak()).isEqualTo(15L);
            assertThat(result.solvedProblemsCount()).isEqualTo(25L);
            assertThat(result.rivalCount()).isEqualTo(3L);
            assertThat(result.categoryRatings()).isEmpty();
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

            UserAccountEntity userAccountEntity = UserAccountEntity.builder()
                .userId(userId)
                .nickname(nickname)
                .build();

            given(userAccountRepository.findByNickname(nickname))
                .willReturn(Optional.of(userAccountEntity));
            given(userStatRepository.findByUserId(userId))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getUserByNickname(nickname))
                .isInstanceOf(UserStatNotFoundException.class)
                .hasMessage("User stats not found for user: " + userId);
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

            UserProfileModifyRequestDto requestDto = UserProfileModifyRequestDto.builder()
                .newNickname(newNickname)
                .newStatusMessage(newStatusMessage)
                .newProfileImageUrl(newProfileImageUrl)
                .build();

            UserAccountEntity userAccountEntity = UserAccountEntity.builder()
                .userId(userId)
                .nickname(currentNickname)
                .statusMessage("Old status")
                .profileImageUrl("old.jpg")
                .build();

            UserStatEntity userStatEntity = UserStatEntity.builder()
                .userId(userId)
                .rating(rating)
                .currentStreak(3L)
                .longestStreak(10L)
                .solvedProblemsCount(15L)
                .rivalCount(2L)
                .build();

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
            assertThat(result).isNotNull();
            assertThat(result.nickname()).isEqualTo(newNickname);
            assertThat(result.statusMessage()).isEqualTo(newStatusMessage);
            assertThat(result.profileImageUrl()).isEqualTo(newProfileImageUrl);
            assertThat(result.ranking()).isEqualTo(ratingRank);
            assertThat(result.rating()).isEqualTo(rating);
            assertThat(result.currentStreak()).isEqualTo(3L);
            assertThat(result.longestStreak()).isEqualTo(10L);
            assertThat(result.solvedProblemsCount()).isEqualTo(15L);
            assertThat(result.rivalCount()).isEqualTo(2L);

            verify(userAccountRepository).save(userAccountEntity);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 수정 시도")
        void modifyUserProfile_UserNotFound() {
            // given
            Long userId = 999L;
            String currentNickname = "oldNickname";
            UserProfileModifyRequestDto requestDto = UserProfileModifyRequestDto.builder()
                .newNickname("newNickname")
                .newStatusMessage("New status")
                .newProfileImageUrl("new.jpg")
                .build();

            given(userAccountRepository.findByUserId(userId))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.modifyUserProfile(userId, currentNickname, requestDto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with id: " + userId);

            verify(userAccountRepository, never()).save(any());
        }

        @Test
        @DisplayName("현재 닉네임과 요청 닉네임이 일치하지 않음")
        void modifyUserProfile_NicknameMismatch() {
            // given
            Long userId = 1L;
            String currentNickname = "oldNickname";
            String actualNickname = "actualNickname";

            UserProfileModifyRequestDto requestDto = UserProfileModifyRequestDto.builder()
                .newNickname("newNickname")
                .newStatusMessage("New status")
                .newProfileImageUrl("new.jpg")
                .build();

            UserAccountEntity userAccountEntity = UserAccountEntity.builder()
                .userId(userId)
                .nickname(actualNickname)
                .build();

            given(userAccountRepository.findByUserId(userId))
                .willReturn(Optional.of(userAccountEntity));

            // when & then
            assertThatThrownBy(() -> userService.modifyUserProfile(userId, currentNickname, requestDto))
                .isInstanceOf(NicknameMismatchException.class);

            verify(userAccountRepository, never()).save(any());
        }

        @Test
        @DisplayName("중복된 닉네임으로 수정 시도")
        void modifyUserProfile_DuplicateNickname() {
            // given
            Long userId = 1L;
            String currentNickname = "oldNickname";
            String duplicateNickname = "existingNickname";

            UserProfileModifyRequestDto requestDto = UserProfileModifyRequestDto.builder()
                .newNickname(duplicateNickname)
                .newStatusMessage("New status")
                .newProfileImageUrl("new.jpg")
                .build();

            UserAccountEntity userAccountEntity = UserAccountEntity.builder()
                .userId(userId)
                .nickname(currentNickname)
                .build();

            given(userAccountRepository.findByUserId(userId))
                .willReturn(Optional.of(userAccountEntity));
            given(userAccountRepository.existsByNickname(duplicateNickname))
                .willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.modifyUserProfile(userId, currentNickname, requestDto))
                .isInstanceOf(DuplicateNicknameException.class)
                .hasMessage("Nickname already in use: " + duplicateNickname);

            verify(userAccountRepository, never()).save(any());
        }

        @Test
        @DisplayName("같은 닉네임으로 수정하는 경우 중복 체크 안함")
        void modifyUserProfile_SameNickname_NoDuplicateCheck() {
            // given
            Long userId = 1L;
            String currentNickname = "sameNickname";

            UserProfileModifyRequestDto requestDto = UserProfileModifyRequestDto.builder()
                .newNickname(currentNickname)
                .newStatusMessage("New status")
                .newProfileImageUrl("new.jpg")
                .build();

            UserAccountEntity userAccountEntity = UserAccountEntity.builder()
                .userId(userId)
                .nickname(currentNickname)
                .statusMessage("Old status")
                .profileImageUrl("old.jpg")
                .build();

            UserStatEntity userStatEntity = UserStatEntity.builder()
                .userId(userId)
                .rating(1000L)
                .build();

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
            verify(userAccountRepository, never()).existsByNickname(any());
            verify(userAccountRepository).save(userAccountEntity);
        }

        @Test
        @DisplayName("프로필 수정 후 사용자 통계 조회 실패")
        void modifyUserProfile_UserStatNotFoundAfterUpdate() {
            // given
            Long userId = 1L;
            String currentNickname = "oldNickname";
            String newNickname = "newNickname";

            UserProfileModifyRequestDto requestDto = UserProfileModifyRequestDto.builder()
                .newNickname(newNickname)
                .newStatusMessage("New status")
                .newProfileImageUrl("new.jpg")
                .build();

            UserAccountEntity userAccountEntity = UserAccountEntity.builder()
                .userId(userId)
                .nickname(currentNickname)
                .build();

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

            verify(userAccountRepository).save(userAccountEntity);
        }
    }

    @Nested
    @DisplayName("엣지 케이스 테스트")
    class EdgeCases {

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
}
