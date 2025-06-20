package com.climbx.climbx.auth;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.climbx.climbx.auth.exception.UnauthorizedException;
import com.climbx.climbx.auth.models.LoginResponse;
import com.climbx.climbx.auth.models.UserSSOInfoResponse;
import com.climbx.climbx.common.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private final String FIXED_TOKEN = "TEST_FIXED_TOKEN";

    @Test
    @DisplayName("OAuth2 리다이렉트 URL 조회 API 테스트")
    void shouldGetOAuth2RedirectUrl() throws Exception {
        // given
        String provider = "GOOGLE";
        given(authService.getAuthorizationUrl(provider)).willReturn(provider);

        // when & then
        mockMvc.perform(get("/api/auth/oauth2/{provider}", provider))
                .andExpect(status().isOk())
                .andExpect(content().string(provider));
    }

    @Test
    @DisplayName("OAuth2 콜백 처리 API 테스트")
    void shouldHandleOAuth2Callback() throws Exception {
        // given
        String provider = "google";
        String code = "auth_code";
        LoginResponse loginResponse = new LoginResponse("Bearer", FIXED_TOKEN, null, 3600L);
        given(authService.handleCallback(provider, code)).willReturn(loginResponse);

        // when & then
        mockMvc.perform(get("/api/auth/callback/{provider}", provider)
                        .header("code", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").value(FIXED_TOKEN))
                .andExpect(jsonPath("$.expiresIn").value(3600));
    }

    @Test
    @DisplayName("토큰 갱신 API 테스트")
    void shouldRefreshToken() throws Exception {
        // given
        String refreshToken = "refreshToken";
        LoginResponse loginResponse = new LoginResponse("Bearer", FIXED_TOKEN, null, 3600L);
        given(authService.refreshAccessToken(refreshToken)).willReturn(loginResponse);
        String json = """
            {
                "refreshToken": "refreshToken"
            }
            """;

        // when & then
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").value(FIXED_TOKEN))
                .andExpect(jsonPath("$.expiresIn").value(3600));
    }

    @Test
    @DisplayName("유효한 토큰으로 사용자 정보 조회 API 테스트")
    void shouldGetCurrentUserInfoWithValidToken() throws Exception {
        // given
        String authHeader = "Bearer " + FIXED_TOKEN;
        Instant now = Instant.now();
        UserSSOInfoResponse userInfo = new UserSSOInfoResponse(
                "user-1", "dummy-user", "GOOGLE", now, now.plusSeconds(3600)
        );
        given(jwtUtil.extractTokenFromHeader(authHeader)).willReturn(FIXED_TOKEN);
        given(authService.getCurrentUserInfo(FIXED_TOKEN)).willReturn(userInfo);

        // when & then
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", authHeader))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("user-1"))
                    .andExpect(jsonPath("$.nickname").value("dummy-user"))
                    .andExpect(jsonPath("$.provider").value("GOOGLE"))
                    .andExpect(jsonPath("$.issuedAt").exists())
                    .andExpect(jsonPath("$.expiresAt").exists());
    }

    @Test
    @DisplayName("Authorization 헤더 없이 사용자 정보 조회 시 400 에러")
    void shouldReturn400WhenAuthorizationHeaderMissing() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("토큰이 비어있을 때 401 에러")
    void shouldReturn401WhenTokenIsEmpty() throws Exception {
        // given
        String authHeader = "Bearer "; // 빈 토큰
        given(jwtUtil.extractTokenFromHeader(authHeader)).willReturn("");

        // when & then
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", authHeader))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 사용자 정보 조회 시 401 에러")
    void shouldReturn401WhenTokenInvalid() throws Exception {
        // given
        String authHeader = "Bearer invalid_token";
        given(jwtUtil.extractTokenFromHeader(authHeader)).willReturn("invalid_token");
        given(authService.getCurrentUserInfo("invalid_token"))
                .willThrow(new UnauthorizedException("Invalid token"));

        // when & then
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", authHeader))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그아웃 API 테스트 - 아무것도 수행하지 않음")
    void shouldSignOut() throws Exception {
        // given
        String authHeader = "Bearer " + FIXED_TOKEN;
        String refreshTokenJson = """
                {
                    "refreshToken": "refresh_token"
                }
                """;

        // when & then
        mockMvc.perform(post("/api/auth/signout")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshTokenJson))
                .andExpect(status().isOk());
    }
} 