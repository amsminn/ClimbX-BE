package com.climbx.climbx.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.climbx.climbx.auth.dto.LoginResponseDto;
import com.climbx.climbx.auth.dto.UserOauth2InfoResponseDto;
import com.climbx.climbx.common.security.JwtContext;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private final String FIXED_TOKEN = "TEST_FIXED_TOKEN";
    @Mock
    private JwtContext jwtUtil;
    @InjectMocks
    private AuthService authService;

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
        LoginResponseDto response = authService.handleCallback("GOOGLE", "auth_code");

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
        LoginResponseDto response = authService.refreshAccessToken("refresh_token");

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
        Long validUserId = 1L;

        // when
        UserOauth2InfoResponseDto response = authService.getCurrentUserInfo(validUserId);

        // then
        assertThat(response.id()).isEqualTo(validUserId);
        assertThat(response.nickname()).isEqualTo("dummy-user");
        assertThat(response.provider()).isEqualTo("GOOGLE");
        assertThat(response.issuedAt()).isNotNull();
        assertThat(response.expiresAt()).isNotNull();
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