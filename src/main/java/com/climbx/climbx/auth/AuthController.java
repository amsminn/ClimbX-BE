package com.climbx.climbx.auth;

import com.climbx.climbx.auth.dto.LoginResponseDto;
import com.climbx.climbx.auth.dto.RefreshRequestDto;
import com.climbx.climbx.auth.dto.UserOauth2InfoResponseDto;
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

     @GetMapping("/oauth2/{provider}")
     public String getOAuth2RedirectUrl(
        @PathVariable("provider") @NotBlank String provider
     ) {
         // 임시구현
         // 실제로 호출되면 안됨.
         return "redirect:" + provider;
     }

     @GetMapping("/oauth2/callback/{provider}")
     public @Valid LoginResponseDto handleOAuth2Callback(
            @PathVariable("provider") @NotBlank String provider,
            @RequestParam("code") @NotBlank String code
     ) {
        return authService.handleCallback(provider, code);
     }

     @PostMapping("/oauth2/refresh")
     public @Valid LoginResponseDto refreshAccessToken(
         @RequestBody @Valid RefreshRequestDto request
     ) {
         return authService.refreshAccessToken(request.refreshToken());
     }

    @GetMapping("/me")
    public @Valid UserOauth2InfoResponseDto getCurrentUserInfo(
        @AuthenticationPrincipal Long userId
    ) {
        return authService.getCurrentUserInfo(userId);
    }

    @PostMapping("/signout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void signOut(
        @RequestBody @Valid RefreshRequestDto request
    ) {
        // 임시 로그인에서 리프레쉬 토큰 드롭 구현 X
    }
}