package com.climbx.climbx.auth;

import com.climbx.climbx.auth.dto.LoginResponseDto;
import com.climbx.climbx.auth.dto.RefreshRequestDto;
import com.climbx.climbx.auth.dto.UserOauth2InfoResponseDto;
import com.climbx.climbx.common.dto.ApiResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @ResponseStatus(HttpStatus.PERMANENT_REDIRECT)
    @GetMapping("/oauth2/{provider}")
    public ApiResponseDto<String> getOAuth2RedirectUrl(
        @PathVariable("provider") @NotBlank String provider
    ) {
        // 임시구현
        // 실제로 호출되면 안됨.
        String redirectUrl = "redirect:" + provider;
        return ApiResponseDto.success(redirectUrl, HttpStatus.PERMANENT_REDIRECT);
    }

    @GetMapping("/oauth2/{provider}/callback")
    public ApiResponseDto<LoginResponseDto> handleOAuth2Callback(
        @PathVariable("provider") @NotBlank String provider,
        @RequestParam("code") @NotBlank String code
    ) {
        LoginResponseDto loginResponse = authService.handleCallback(provider, code);
        return ApiResponseDto.success(loginResponse);
    }

    @PostMapping("/oauth2/refresh")
    public ApiResponseDto<LoginResponseDto> refreshAccessToken(
        @RequestBody @Valid RefreshRequestDto request
    ) {
        LoginResponseDto refreshResponse = authService.refreshAccessToken(request.refreshToken());
        return ApiResponseDto.success(refreshResponse);
    }

    @GetMapping("/me")
    public ApiResponseDto<UserOauth2InfoResponseDto> getCurrentUserInfo(
        @AuthenticationPrincipal Long userId
    ) {
        UserOauth2InfoResponseDto userInfo = authService.getCurrentUserInfo(userId);
        return ApiResponseDto.success(userInfo);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/signout")
    public ApiResponseDto<Void> signOut(
        @RequestBody @Valid RefreshRequestDto request
    ) {
        // 임시 로그인에서 리프레쉬 토큰 드롭 구현 X
//        return ApiResponseDto.success(null, "로그아웃이 완료되었습니다.");
        return ApiResponseDto.success(null, HttpStatus.NO_CONTENT);
    }
}