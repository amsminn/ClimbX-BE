package com.climbx.climbx.auth;

import com.climbx.climbx.auth.exception.UnauthorizedException;
import com.climbx.climbx.auth.models.LoginResponse;
import com.climbx.climbx.auth.models.UserSSOInfoResponse;
import com.climbx.climbx.common.security.JwtUtil;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private static final String FIXED_USER_ID = "user-1";
    private static final String DUMMY_USERNAME = "dummy-user";
    private static final String DUMMY_PROVIDER = "GOOGLE";

    public AuthService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

     public String getAuthorizationUrl(String provider) {
         return provider; // 임시 구현: 실제로는 OAuth2 리다이렉트 URL을 반환해야 함
     }

     public LoginResponse handleCallback(String provider, String code) {
         String token = jwtUtil.generateFixedToken();
         return new LoginResponse("Bearer", token, null, 3600L);
     }

    public LoginResponse refreshAccessToken(String refreshToken) {
        String token = jwtUtil.generateFixedToken();
        return new LoginResponse("Bearer", token, null, 3600L);
    }

    public UserSSOInfoResponse getCurrentUserInfo(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new UnauthorizedException("Invalid token");
        }
        
        return new UserSSOInfoResponse(
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