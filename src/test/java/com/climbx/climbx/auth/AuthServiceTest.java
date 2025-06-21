package com.climbx.climbx.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.climbx.climbx.auth.exception.UserUnauthorizedException;
import com.climbx.climbx.auth.models.LoginResponse;
import com.climbx.climbx.auth.models.UserOauth2InfoResponse;
import com.climbx.climbx.common.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    private AuthService authService;
    private final String FIXED_TOKEN = "TEST_FIXED_TOKEN";

    @BeforeEach
    void setUp() {
        authService = new AuthService(jwtUtil);
    }

    @Test
    @DisplayName("authorization URL을 요청하면 provider를 그대로 반환한다")
    void shouldReturnEmptyStringForAuthorizationUrl() {
        // when
        String url = authService.getAuthorizationUrl("GOOGLE");

        // then
        assertThat(url).isEqualTo("GOOGLE");
    }

    @Test
    @DisplayName("콜백 처리 시 고정 토큰을 반환한다")
    void shouldHandleCallbackAndReturnFixedToken() {
        // given
        given(jwtUtil.generateFixedToken()).willReturn(FIXED_TOKEN);

        // when
        LoginResponse response = authService.handleCallback("GOOGLE", "auth_code");

        // then
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.accessToken()).isEqualTo(FIXED_TOKEN);
        assertThat(response.refreshToken()).isNull();
        assertThat(response.expiresIn()).isEqualTo(3600L);
        
        verify(jwtUtil).generateFixedToken();
    }

    @Test
    @DisplayName("토큰 갱신 시 고정 토큰을 반환한다")
    void shouldRefreshTokenAndReturnFixedToken() {
        // given
        given(jwtUtil.generateFixedToken()).willReturn(FIXED_TOKEN);

        // when
        LoginResponse response = authService.refreshAccessToken("refresh_token");

        // then
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.accessToken()).isEqualTo(FIXED_TOKEN);
        assertThat(response.refreshToken()).isNull();
        assertThat(response.expiresIn()).isEqualTo(3600L);
        
        verify(jwtUtil).generateFixedToken();
    }

    @Test
    @DisplayName("유효한 user-id로 사용자 정보를 조회한다")
    void shouldGetCurrentUserInfoWithValidToken() {
        // given
        String validUserId = "user-1";

        // when
        UserOauth2InfoResponse response = authService.getCurrentUserInfo(validUserId);

        // then
        assertThat(response.id()).isEqualTo("user-1");
        assertThat(response.nickname()).isEqualTo("dummy-user");
        assertThat(response.provider()).isEqualTo("GOOGLE");
        assertThat(response.issuedAt()).isNotNull();
        assertThat(response.expiresAt()).isNotNull();
    }

    @Test
    @DisplayName("유효하지 않은 user-id로 사용자 정보 조회 시 예외가 발생한다")
    void shouldThrowExceptionForInvalidToken() {
        // given
        String invalidUserId = "invalid_user_id";

        // when & then
        assertThatThrownBy(() -> authService.getCurrentUserInfo(invalidUserId))
                .isInstanceOf(UserUnauthorizedException.class)
                .hasMessage("Unauthorized user");
//        UnauthorizedException e = assertThrows(UnauthorizedException.class, () -> authService.getCurrentUserInfo(invalidUserId));
//        assertThat(e.getMessage()).isEqualTo("Unauthorized user");
    }

    @Test
    @DisplayName("signOut 메서드는 아무것도 수행하지 않는다")
    @Disabled
    void shouldDoNothingWhenSignOut() {
        // when
        authService.signOut("some_token");

        // then
        // 아무것도 수행하지 않음을 확인 (예외가 발생하지 않음)
    }
} 