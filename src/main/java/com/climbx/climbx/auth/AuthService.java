package com.climbx.climbx.auth;

import com.climbx.climbx.auth.exception.UserUnauthorizedException;
import com.climbx.climbx.auth.models.LoginResponse;
import com.climbx.climbx.auth.models.UserOauth2InfoResponse;
import com.climbx.climbx.common.security.JwtUtil;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private static final String FIXED_USER_ID = "user-1";
    private static final String DUMMY_USERNAME = "dummy-user";
    private static final String DUMMY_PROVIDER = "GOOGLE";

     public String getAuthorizationUrl(String provider) {
         return provider; // 임시 구현: 실제로는 OAuth2 리다이렉트 URL을 반환해야 함
     }

     public LoginResponse handleCallback(String provider, String code) {
         String token = jwtUtil.generateFixedToken();
         return new LoginResponse("Bearer", token, null, 3600L);
     }

    public LoginResponse refreshAccessToken(String sub) {
        String token = jwtUtil.generateFixedToken();
        return new LoginResponse("Bearer", token, null, 3600L);
    }

    public UserOauth2InfoResponse getCurrentUserInfo(String userId) {
        if (!userId.equals(FIXED_USER_ID)) {
            throw new UserUnauthorizedException("Unauthorized user");
        }
        return new UserOauth2InfoResponse(
            FIXED_USER_ID,
            DUMMY_USERNAME,
            DUMMY_PROVIDER,
            Instant.now(),
            Instant.now().plusSeconds(3600)
        );
    }

    public void signOut(String token) {
        // 임시 로그인에서 로그아웃은 구현하지 않음
    }
}