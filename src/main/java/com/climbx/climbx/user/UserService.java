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
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class UserService {

    private final UserAccountRepository userAccountRepository;
    private final UserStatRepository userStatRepository;

    private UserProfileResponseDto getUserById(Long userId) {
        UserAccountEntity userAccountEntity = findUserById(userId);
        return buildProfile(userAccountEntity);
    }

    public UserProfileResponseDto getUserByNickname(String nickname) {
        UserAccountEntity userAccountEntity = findUserByNickname(nickname);
        return buildProfile(userAccountEntity);
    }

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
        Map<String, Long> categoryRatings = Collections.emptyMap();

        return new UserProfileResponseDto(
            userAccount.nickname(),
            userAccount.statusMessage(),
            userAccount.profileImageUrl(),
            ratingRank,
            userStat.rating(),
            categoryRatings,
            userStat.currentStreak(),
            userStat.longestStreak(),
            userStat.solvedProblemsCount(),
            userStat.rivalCount()
        );
    }

    private UserAccountEntity findUserById(Long userId) {
        return userAccountRepository.findByUserId(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private UserAccountEntity findUserByNickname(String nickname) {
        return userAccountRepository.findByNickname(nickname)
            .orElseThrow(() -> new UserNotFoundException(nickname));
    }

    private UserStatEntity findUserStatByUserId(Long userId) {
        return userStatRepository.findByUserId(userId)
            .orElseThrow(() -> new UserStatNotFoundException(userId));
    }
}
