package com.climbx.climbx.auth;

import com.climbx.climbx.auth.dto.LoginResponseDto;
import com.climbx.climbx.auth.dto.RefreshRequestDto;
import com.climbx.climbx.auth.dto.UserOauth2InfoResponseDto;
import com.climbx.climbx.common.annotation.SuccessStatus;
import com.climbx.climbx.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApiDocumentation {

    private final AuthService authService;

    /**
     * OAuth2 콜백을 처리합니다. 모든 OAuth2 제공자의 인가 코드를 받아 사용자 인증을 완료하고 JWT 토큰을 발급합니다. URL 생성, 반드시 dev 환경에서
     * 소셜 로그인 테스트 시에만 사용
     */
    @GetMapping("/oauth2/kakao/authorize-url")
    public ApiResponse<String> getKakaoAuthorizeUrl() {
        log.info("카카오 OAuth2 인증 URL 요청");

        String authorizeUrl = authService.generateKakaoAuthorizeUrl();

        log.info("카카오 OAuth2 인증 URL 생성 완료");
        return ApiResponse.success(authorizeUrl, HttpStatus.OK);
    }

    /**
     * provider의 인가 code를 받아 인증하고 토큰 발급
     */
    @GetMapping("/oauth2/{provider}/callback")
    @SuccessStatus(value = HttpStatus.OK)
    public LoginResponseDto handleOAuth2Callback(
        @PathVariable("provider") String provider,
        @RequestParam("code") String code
    ) {
        /*
         * code를 로깅하지 않도록 주의해야함
         */
        log.info(
            "{} OAuth2 콜백 처리 시작: code={}",
            provider.toUpperCase(),
            code.substring(0, Math.min(code.length(), 10)) + "..."
        );

        LoginResponseDto response = authService.handleCallback(provider, code);

        log.info("{} OAuth2 콜백 처리 완료", provider.toUpperCase());
        return response;
    }

    /**
     * 액세스 토큰을 갱신합니다. 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.
     */
    @PostMapping("/oauth2/refresh")
    @SuccessStatus(value = HttpStatus.CREATED)
    public LoginResponseDto refreshAccessToken(@RequestBody RefreshRequestDto request) {
        log.info("액세스 토큰 갱신 요청");

        LoginResponseDto refreshResponse = authService.refreshAccessToken(request.refreshToken());

        log.info("액세스 토큰 갱신 완료");
        return refreshResponse;
    }

    /**
     * 현재 사용자의 SSO 관련 정보만 조회
     */
    @GetMapping("/me")
    @SuccessStatus(value = HttpStatus.OK)
    public UserOauth2InfoResponseDto getCurrentUserInfo(
        @AuthenticationPrincipal Long userId
    ) {
        log.info("현재 사용자 정보 조회: userId={}", userId);

        UserOauth2InfoResponseDto response = authService.getCurrentUserInfo(userId);

        log.info("현재 사용자 정보 조회 완료: nickname={}", response.nickname());
        return response;
    }

    /**
     * 사용자 로그아웃을 처리합니다. 클라이언트에서 토큰을 삭제해주세요. 클라이언트에서도 토큰 삭제 필요
     */
    @PostMapping("/signout")
    @SuccessStatus(value = HttpStatus.NO_CONTENT)
    public void signOut(@RequestBody RefreshRequestDto request) {
        log.info("로그아웃 요청");

        authService.signOut(request.refreshToken());

        log.info("로그아웃 완료");
    }
}