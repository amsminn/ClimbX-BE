package com.climbx.climbx.user;

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
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class UserService {

    private final UserAccountRepository userAccountRepository;
    private final UserStatRepository userStatRepository;

    @Transactional(readOnly = true)
    public UserProfileResponseDto getUserById(Long userId) {
        UserAccountEntity userAccountEntity = findUserById(userId);
        return buildProfile(userAccountEntity);
    }

    @Transactional(readOnly = true)
    public UserProfileResponseDto getUserByNickname(String nickname) {
        UserAccountEntity userAccountEntity = findUserByNickname(nickname);
        return buildProfile(userAccountEntity);
    }

    @Transactional
    public UserProfileResponseDto modifyUserProfile(
        Long userId,
        String currentNickname,
        UserProfileModifyRequestDto userProfileDto
    ) {
        UserAccountEntity userAccountEntity = findUserById(userId);

        if (!currentNickname.equals(userAccountEntity.nickname())) {
            throw new NicknameMismatchException(currentNickname, userAccountEntity.nickname());
        }

        if (!currentNickname.equals(userProfileDto.newNickname()) &&
            userAccountRepository.existsByNickname(userProfileDto.newNickname())) {
            throw new DuplicateNicknameException(userProfileDto.newNickname());
        }

        userAccountEntity.modifyProfile(
            userProfileDto.newNickname(),
            userProfileDto.newStatusMessage(),
            userProfileDto.newProfileImageUrl()
        );
        userAccountRepository.save(userAccountEntity);

        return getUserById(userId);
    }

    private UserProfileResponseDto buildProfile(UserAccountEntity userAccount) {
        UserStatEntity userStat = findUserStatByUserId(userAccount.userId());
        Long ratingRank = userStatRepository.findRatingRank(userStat.rating());

        return UserProfileResponseDto.builder()
            .nickname(userAccount.nickname())
            .statusMessage(userAccount.statusMessage())
            .profileImageUrl(userAccount.profileImageUrl())
            .ranking(ratingRank)
            .rating(userStat.rating())
            .categoryRatings(Collections.emptyMap())
            .currentStreak(userStat.currentStreak())
            .longestStreak(userStat.longestStreak())
            .solvedProblemsCount(userStat.solvedProblemsCount())
            .rivalCount(userStat.rivalCount())
            .build();
    }

    private UserAccountEntity findUserById(Long userId) {
        return userAccountRepository.findByUserId(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private UserAccountEntity findUserByNickname(String nickname) {
        return userAccountRepository.findByNickname(nickname)
            .orElseThrow(() -> new UserNotFoundException(nickname));
    }

    protected UserStatEntity findUserStatByUserId(Long userId) {
        return userStatRepository.findByUserId(userId)
            .orElseThrow(() -> new UserStatNotFoundException(userId));
    }
}
