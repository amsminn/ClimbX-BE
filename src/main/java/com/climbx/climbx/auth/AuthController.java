package com.climbx.climbx.auth;

import com.climbx.climbx.auth.exception.UnauthorizedException;
import com.climbx.climbx.auth.models.LoginResponse;
import com.climbx.climbx.auth.models.RefreshRequest;
import com.climbx.climbx.auth.models.UserSSOInfoResponse;
import com.climbx.climbx.common.security.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

     @GetMapping("/oauth2/{provider}")
     public String getOAuth2RedirectUrl(
        @PathVariable("provider") @NotNull String provider
     ) {
         // 임시구현
         // 실제로 호출되면 안됨.
         return authService.getAuthorizationUrl(provider);
     }

     @GetMapping("/callback/{provider}")
     public LoginResponse handleOAuth2Callback(
            @PathVariable("provider") @NotBlank String provider,
            @RequestHeader("code") String code
     ) {
         return authService.handleCallback(provider, code);
     }

     @PostMapping("/refresh")
     public LoginResponse refreshAccessToken(
         @RequestBody @Valid RefreshRequest request
     ) {
         return authService.refreshAccessToken(request.refreshToken());
     }

    @GetMapping("/me")
    public UserSSOInfoResponse getCurrentUserInfo(
        @RequestHeader("Authorization") @NotBlank String authHeader
    ) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);

        if (token.isEmpty()) {
            throw new UnauthorizedException("Token is missing");
        }

        return authService.getCurrentUserInfo(token);
    }

    @PostMapping("/signout")
    public void signOut(
        @RequestHeader("Authorization") @NotBlank String authHeader,
        @RequestBody @Valid RefreshRequest request
    ) {
        // 임시 로그인에서 리프레쉬 토큰 드롭 구현 X
    }
}