package com.climbx.climbx.admin.user.service;

import com.climbx.climbx.user.dto.UserProfileResponseDto;
import com.climbx.climbx.user.entity.UserAccountEntity;
import com.climbx.climbx.user.entity.UserStatEntity;
import com.climbx.climbx.user.exception.UserNotFoundException;
import com.climbx.climbx.user.repository.UserAccountRepository;
import com.climbx.climbx.user.repository.UserStatRepository;
import com.climbx.climbx.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AdminUserService {

    private final UserAccountRepository userAccountRepository;
    private final UserStatRepository userStatRepository;
    private final UserService userService;

    @Transactional
    public UserProfileResponseDto updateRating(String nickname, Integer rating) {
        UserAccountEntity userAccount = userAccountRepository.findByNickname(nickname)
            .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + nickname));

        Long userId = userAccount.userId();

        UserStatEntity userStat = userStatRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("사용자 통계 정보를 찾을 수 없습니다: " + userId));

        userStat.setRating(rating);

        return userService.buildProfile(userAccount);
    }
}
