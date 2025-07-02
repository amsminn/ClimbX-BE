package com.climbx.climbx.auth;

import com.climbx.climbx.auth.dto.LoginResponseDto;
import com.climbx.climbx.auth.dto.UserOauth2InfoResponseDto;
import com.climbx.climbx.common.security.JwtContext;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Long FIXED_USER_ID = 1L;
    private static final String DUMMY_USERNAME = "dummy-user";
    private static final String DUMMY_PROVIDER = "GOOGLE";
    private final JwtContext jwtUtil;

    public String getAuthorizationUrl(String provider) {
        return provider; // 임시 구현: 실제로는 OAuth2 리다이렉트 URL을 반환해야 함
    }

    public LoginResponseDto handleCallback(String provider, String code) {
        String token = jwtUtil.generateFixedToken();
        return LoginResponseDto.builder()
            .tokenType("Bearer")
            .accessToken(token)
            .refreshToken(null)
            .expiresIn(3600L)
            .build();
    }

    public LoginResponseDto refreshAccessToken(String sub) {
        String token = jwtUtil.generateFixedToken();
        return LoginResponseDto.builder()
            .tokenType("Bearer")
            .accessToken(token)
            .refreshToken(null)
            .expiresIn(3600L)
            .build();
    }

    public UserOauth2InfoResponseDto getCurrentUserInfo(Long userId) {
        return UserOauth2InfoResponseDto.builder()
            .id(FIXED_USER_ID)
            .nickname(DUMMY_USERNAME)
            .provider(DUMMY_PROVIDER)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();
    }

    public void signOut(String token) {
        // 임시 로그인에서 로그아웃은 구현하지 않음
    }
}