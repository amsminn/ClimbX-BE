package com.climbx.climbx.user;

import com.climbx.climbx.common.enums.UserHistoryCriteriaType;
import com.climbx.climbx.problem.dto.ProblemResponseDto;
import com.climbx.climbx.user.dto.DailyHistoryResponseDto;
import com.climbx.climbx.user.dto.UserProfileModifyRequestDto;
import com.climbx.climbx.user.dto.UserProfileResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Validated
@RequiredArgsConstructor
class UserController {

    private final UserService userService;

    @GetMapping("")
    public List<@Valid UserProfileResponseDto> getUsers(
        @RequestParam(name = "search", required = false) String search
    ) {
        return userService.getUsers(search);
    }

    @GetMapping("/{nickname}")
    public @Valid UserProfileResponseDto getUserByNickname(
        @PathVariable @NotBlank String nickname
    ) {
        return userService.getUserByNickname(nickname);
    }

    @PutMapping("/{nickname}")
    public @Valid UserProfileResponseDto modifyUserProfile(
        @AuthenticationPrincipal Long userId,
        @PathVariable @NotBlank String nickname,
        @RequestBody @Valid UserProfileModifyRequestDto request
    ) {
        return userService.modifyUserProfile(
            userId, nickname, request
        );
    }

    @GetMapping("/{nickname}/top-problems")
    public List<@Valid ProblemResponseDto> getUserTopProblems(
        @PathVariable @NotBlank String nickname,
        @RequestParam(name = "limit", required = false, defaultValue = "20") @Min(1) @Max(20) Integer limit
    ) {
        return userService.getUserTopProblems(nickname, limit);
    }

    @GetMapping("/{nickname}/streak")
    public List<@Valid DailyHistoryResponseDto> getUserStreak(
        @PathVariable @NotBlank String nickname,
        @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return userService.getUserStreak(nickname, from, to);
    }

    @GetMapping("/{nickname}/history")
    public List<@Valid DailyHistoryResponseDto> getUserDailyHistory(
        @PathVariable @NotBlank String nickname,
        @RequestParam(name = "criteria", required = true) @NotNull UserHistoryCriteriaType criteria,
        @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return userService.getUserDailyHistory(nickname, criteria, from, to);
    }
}
