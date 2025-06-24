package com.climbx.climbx.user;

import com.climbx.climbx.user.dto.UserProfileModifyRequestDto;
import com.climbx.climbx.user.dto.UserProfileResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Validated
@RequiredArgsConstructor
class UserController {

    private final UserService userService;

    @GetMapping("/{nickname}")
    public ResponseEntity<@Valid UserProfileResponseDto> getUserByNickname(
        @PathVariable @NotBlank String nickname
    ) {
        UserProfileResponseDto response = userService.getUserByNickname(nickname);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{nickname}")
    public ResponseEntity<@Valid UserProfileResponseDto> modifyUserProfile(
        @AuthenticationPrincipal Long userId,
        @PathVariable @NotBlank String nickname,
        @RequestBody @Valid UserProfileModifyRequestDto request
    ) {
        UserProfileResponseDto response = userService.modifyUserProfile(
            userId, nickname, request
        );
        return ResponseEntity.ok(response);
    }
}
