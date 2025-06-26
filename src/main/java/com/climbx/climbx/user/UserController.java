package com.climbx.climbx.user;

import com.climbx.climbx.problem.dto.ProblemResponseDto;
import com.climbx.climbx.user.dto.UserProfileModifyRequestDto;
import com.climbx.climbx.user.dto.UserProfileResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
}
