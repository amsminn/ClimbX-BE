package com.climbx.climbx.user;

import com.climbx.climbx.user.dto.UserProfileRequestDto;
import com.climbx.climbx.user.dto.UserProfileResponseDto;
import com.climbx.climbx.user.entity.UserAccount;
import com.climbx.climbx.user.entity.UserStat;
import com.climbx.climbx.user.exception.DuplicateNicknameException;
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

    private UserProfileResponseDto getUserById(Long userId)
        throws UserNotFoundException, UserStatNotFoundException {
        UserAccount userAccount = findUserById(userId);
        UserStat userStat = findUserStatByUserId(userId);

        Long ratingRank = userStatRepository.findRatingRank(userStat.rating());
        Map<String, Long> categoryRatings = Collections.emptyMap(); // TODO: 분야별 레이팅은 추후 구현 예정

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

    public UserProfileResponseDto getUserByNickname(String nickname)
        throws UserNotFoundException, UserStatNotFoundException {
        Long userId = findUserByNickname(nickname).userId();
        return getUserById(userId);
    }

    public UserProfileResponseDto modifyUserProfile(
        Long userId,
        UserProfileRequestDto userProfileDto
    ) throws UserNotFoundException, DuplicateNicknameException {
        UserAccount userAccount = findUserById(userId);

        if (userAccountRepository.existsByNickname(userProfileDto.nickname())) {
            throw new DuplicateNicknameException(userProfileDto.nickname());
        }

        userAccount.nickname(userProfileDto.nickname());
        userAccount.statusMessage(userProfileDto.statusMessage());
        userAccount.profileImageUrl(userProfileDto.profileImageUrl());
        userAccountRepository.save(userAccount);

        return getUserById(userId);
    }

    private UserAccount findUserById(Long userId) {
        return userAccountRepository.findByUserId(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private UserAccount findUserByNickname(String nickname) {
        return userAccountRepository.findByNickname(nickname)
            .orElseThrow(() -> new UserNotFoundException(nickname));
    }

    private UserStat findUserStatByUserId(Long userId) {
        return userStatRepository.findByUserId(userId)
            .orElseThrow(() -> new UserStatNotFoundException(userId));
    }
}
