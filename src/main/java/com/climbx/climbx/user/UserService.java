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

    private UserProfileResponseDto getUserById(Long userId)
        throws UserNotFoundException, UserStatNotFoundException {
        UserAccountEntity userAccountEntity = findUserById(userId);
        UserStatEntity userStatEntity = findUserStatByUserId(userId);

        Long ratingRank = userStatRepository.findRatingRank(userStatEntity.rating());
        Map<String, Long> categoryRatings = Collections.emptyMap(); // TODO: 분야별 레이팅은 추후 구현 예정

        return new UserProfileResponseDto(
            userAccountEntity.nickname(),
            userAccountEntity.statusMessage(),
            userAccountEntity.profileImageUrl(),
            ratingRank,
            userStatEntity.rating(),
            categoryRatings,
            userStatEntity.currentStreak(),
            userStatEntity.longestStreak(),
            userStatEntity.solvedProblemsCount(),
            userStatEntity.rivalCount()
        );
    }

    public UserProfileResponseDto getUserByNickname(String nickname) {
        Long userId = findUserByNickname(nickname).userId();
        return getUserById(userId);
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

//        userAccountEntity.nickname(userProfileDto.newNickname());
        userAccountEntity.modifyProfile(
            userProfileDto.newNickname(),
            userProfileDto.newStatusMessage(),
            userProfileDto.newProfileImageUrl()
        );
        userAccountRepository.save(userAccountEntity);

        return getUserById(userId);
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
