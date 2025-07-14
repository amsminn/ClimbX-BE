package com.climbx.climbx.auth;

import com.climbx.climbx.auth.dto.CallbackRequestDto;
import com.climbx.climbx.auth.dto.CallbackResponseDto;
import com.climbx.climbx.auth.dto.RefreshRequestDto;
import com.climbx.climbx.auth.dto.RefreshResponseDto;
import com.climbx.climbx.auth.dto.UserAuthResponseDto;
import com.climbx.climbx.common.annotation.SuccessStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApiDocumentation {

    private final AuthService authService;

    /**
     * provider의 인가 code를 받아 인증하고 토큰 발급
     */
    @Override
    @PostMapping("/oauth2/{provider}/callback")
    @SuccessStatus(value = HttpStatus.CREATED)
    public CallbackResponseDto handleOAuth2Callback(
        @PathVariable String provider,
        @RequestBody CallbackRequestDto request

    ) {
        log.info(
            "{} OAuth2 콜백 처리 시작",
            provider.toUpperCase()
        );

        CallbackResponseDto response = authService.handleCallback(provider, request);

        log.info("{} OAuth2 콜백 처리 완료", provider.toUpperCase());
        
        return response;
    }

    /**
     * 액세스 토큰을 갱신합니다. 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.
     */
    @Override
    @PostMapping("/oauth2/refresh")
    @SuccessStatus(value = HttpStatus.CREATED)
    public RefreshResponseDto refreshAccessToken(@RequestBody RefreshRequestDto request) {
        log.info("액세스 토큰 갱신 요청");

        RefreshResponseDto refreshResponse = authService.refreshAccessToken(request.refreshToken());

        log.info("액세스 토큰 갱신 완료");
        return refreshResponse;
    }

    /**
     * 현재 사용자의 SSO 관련 정보만 조회
     */
    @Override
    @GetMapping("/me")
    @SuccessStatus(value = HttpStatus.OK)
    public UserAuthResponseDto getCurrentUserInfo(
        @AuthenticationPrincipal Long userId
    ) {
        log.info("현재 사용자 정보 조회: userId={}", userId);

        UserAuthResponseDto response = authService.getCurrentUserInfo(userId);

        log.info("현재 사용자 정보 조회 완료: nickname={}", response.nickname());
        return response;
    }

    /**
     * 사용자 로그아웃을 처리합니다. 클라이언트에서 토큰을 삭제해주세요. 클라이언트에서도 토큰 삭제 필요
     */
    @Override
    @PostMapping("/signout")
    @SuccessStatus(value = HttpStatus.NO_CONTENT)
    public void signOut(@RequestBody RefreshRequestDto request) {
        log.info("로그아웃 요청");

        authService.signOut(request.refreshToken());

        log.info("로그아웃 완료");
    }
}