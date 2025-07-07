package com.climbx.climbx.auth;

import com.climbx.climbx.auth.dto.LoginResponseDto;
import com.climbx.climbx.auth.dto.RefreshRequestDto;
import com.climbx.climbx.auth.dto.UserOauth2InfoResponseDto;
import com.climbx.climbx.common.annotation.SuccessStatus;
import com.climbx.climbx.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApiDocumentation {

    private final AuthService authService;

    @GetMapping("/oauth2/{provider}")
    public ResponseEntity<ApiResponse<Void>> getOAuth2RedirectUrl(
        @PathVariable("provider") String provider
    ) {
        // 임시구현
        // 실제로 호출되면 안됨.
        String redirectUrl = "https://www.naver.com";
        return ResponseEntity.status(HttpStatus.FOUND)
            .location(java.net.URI.create(redirectUrl))
            .body(ApiResponse.success(null, "Redirecting to OAuth2 provider"));
    }

    @GetMapping("/oauth2/{provider}/callback")
    @SuccessStatus(value = HttpStatus.OK)
    public LoginResponseDto handleOAuth2Callback(
        @PathVariable("provider") String provider,
        @RequestParam("code") String code
    ) {
        return authService.handleCallback(provider, code);
    }

    @PostMapping("/oauth2/refresh")
    @SuccessStatus(value = HttpStatus.CREATED)
    public LoginResponseDto refreshAccessToken(@RequestBody RefreshRequestDto request) {
        return authService.refreshAccessToken(request.refreshToken());
    }

    @GetMapping("/me")
    @SuccessStatus(value = HttpStatus.OK)
    public UserOauth2InfoResponseDto getCurrentUserInfo(
        @AuthenticationPrincipal Long userId
    ) {
        return authService.getCurrentUserInfo(userId);
    }

    @PostMapping("/signout")
    @SuccessStatus(value = HttpStatus.NO_CONTENT)
    public void signOut(@RequestBody RefreshRequestDto request) {
        // 임시 로그인에서 리프레쉬 토큰 드롭 구현 X
    }
}