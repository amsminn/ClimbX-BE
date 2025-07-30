package com.climbx.climbx.user;

import com.climbx.climbx.common.annotation.SuccessStatus;
import com.climbx.climbx.problem.dto.ProblemDetailsResponseDto;
import com.climbx.climbx.user.dto.DailyHistoryResponseDto;
import com.climbx.climbx.user.dto.UserProfileModifyRequestDto;
import com.climbx.climbx.user.dto.UserProfileResponseDto;
import com.climbx.climbx.user.enums.CriteriaType;
import com.climbx.climbx.user.service.UserService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
class UserController implements UserApiDocumentation {

    private final UserService userService;

    @Override
    @GetMapping
    @SuccessStatus(value = HttpStatus.OK)
    public List<UserProfileResponseDto> getUsers(
        @RequestParam(name = "search", required = false)
        String search
    ) {
        log.info("사용자 목록 조회: search={}", search);
        return userService.getUsers(search);
    }

    @Override
    @GetMapping("/{nickname}")
    @SuccessStatus(value = HttpStatus.OK)
    public UserProfileResponseDto getUserByNickname(@PathVariable String nickname) {
        log.info("사용자 프로필 조회: nickname={}", nickname);
        return userService.getUserByNickname(nickname);
    }

    @Override
    @PutMapping("/{nickname}")
    @SuccessStatus(value = HttpStatus.OK)
    public UserProfileResponseDto modifyUserProfile(
        @AuthenticationPrincipal
        Long userId,

        @PathVariable
        String nickname,

        @RequestBody
        UserProfileModifyRequestDto request
    ) {
        log.info("사용자 프로필 수정: userId={}, nickname={}", userId, nickname);
        return userService.modifyUserProfile(
            userId,
            nickname,
            request
        );
    }

    @Override
    @GetMapping("/{nickname}/top-problems")
    @SuccessStatus(value = HttpStatus.OK)
    public List<ProblemDetailsResponseDto> getUserTopProblems(
        @PathVariable
        String nickname,

        @RequestParam(name = "limit", required = false, defaultValue = "20")
        Integer limit
    ) {
        log.info("사용자 상위 문제 조회: nickname={}, limit={}", nickname, limit);
        return userService.getUserTopProblems(nickname, limit);
    }

    @Override
    @GetMapping("/{nickname}/streak")
    @SuccessStatus(value = HttpStatus.OK)
    public List<DailyHistoryResponseDto> getUserStreak(
        @PathVariable
        String nickname,

        @RequestParam(name = "from", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate from,

        @RequestParam(name = "to", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate to
    ) {
        log.info("사용자 스트릭 조회: nickname={}, from={}, to={}", nickname, from, to);
        return userService.getUserStreak(nickname, from, to);
    }

    @Override
    @GetMapping("/{nickname}/history")
    @SuccessStatus(value = HttpStatus.OK)
    public List<DailyHistoryResponseDto> getUserDailyHistory(
        @PathVariable
        String nickname,

        @RequestParam(name = "criteria", required = true)
        CriteriaType criteria,

        @RequestParam(name = "from", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate from,

        @RequestParam(name = "to", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate to
    ) {
        log.info("사용자 히스토리 그래프 데이터 조회: nickname={}, criteria={}, from={}, to={}",
            nickname, criteria, from, to);
        return userService.getUserDailyHistory(nickname, criteria, from, to);
    }
}
