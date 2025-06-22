package com.climbx.climbx.auth;

import com.climbx.climbx.auth.dto.LoginResponseDto;
import com.climbx.climbx.auth.dto.RefreshRequestDto;
import com.climbx.climbx.auth.dto.UserOauth2InfoResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     public ResponseEntity<Void> getOAuth2RedirectUrl(
        @PathVariable("provider") @NotBlank String provider
     ) {
         // 임시구현
         // 실제로 호출되면 안됨.
         URI redirectUri = URI.create(authService.getAuthorizationUrl(provider));
         return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
     }

     @GetMapping("/oauth2/callback/{provider}")
     public ResponseEntity<LoginResponseDto> handleOAuth2Callback(
            @PathVariable("provider") @NotBlank String provider,
            @RequestParam("code") @NotBlank String code
     ) {
        LoginResponseDto resp = authService.handleCallback(provider, code);
        return ResponseEntity.ok(resp);
     }

     @PostMapping("/oauth2/refresh")
     public ResponseEntity<LoginResponseDto> refreshAccessToken(
         @RequestBody @Valid RefreshRequestDto request
     ) {
         LoginResponseDto resp = authService.refreshAccessToken(request.refreshToken());
         return ResponseEntity.ok(resp);
     }

    @GetMapping("/me")
    public ResponseEntity<UserOauth2InfoResponseDto> getCurrentUserInfo(
        @AuthenticationPrincipal Long userId
    ) {
        UserOauth2InfoResponseDto resp = authService.getCurrentUserInfo(userId);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/signout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void signOut(
        @RequestBody @Valid RefreshRequestDto request
    ) {
        // 임시 로그인에서 리프레쉬 토큰 드롭 구현 X
    }
}