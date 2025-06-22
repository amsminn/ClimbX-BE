package com.climbx.climbx.auth;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.climbx.climbx.auth.dto.LoginResponseDto;
import com.climbx.climbx.common.security.JwtUtil;
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

    private final String FIXED_TOKEN = "TEST_FIXED_TOKEN";

    @Test
    @DisplayName("OAuth2 리다이렉트 URL 조회 API 테스트")
    void shouldGetOAuth2RedirectUrl() throws Exception {
        // given
        String provider = "GOOGLE";
        given(authService.getAuthorizationUrl(provider)).willReturn(provider);

        // when & then
        mockMvc.perform(get("/api/auth/oauth2/{provider}", provider))
                .andExpect(status().isFound());
    }

    @Test
    @DisplayName("OAuth2 콜백 처리 API 테스트")
    void shouldHandleOAuth2Callback() throws Exception {
        // given
        String provider = "google";
        String code = "auth_code";
        LoginResponseDto loginResponse = new LoginResponseDto("Bearer", FIXED_TOKEN, null, 3600L);
        given(authService.handleCallback(provider, code)).willReturn(loginResponse);

        // when & then
        mockMvc.perform(get("/api/auth/oauth2/callback/{provider}", provider)
                        .param("code", code))
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
        LoginResponseDto loginResponse = new LoginResponseDto("Bearer", FIXED_TOKEN, null, 3600L);
        given(authService.refreshAccessToken(refreshToken)).willReturn(loginResponse);
        String json = """
            {
                "refreshToken": "refreshToken"
            }
            """;

        // when & then
        mockMvc.perform(post("/api/auth/oauth2/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").value(FIXED_TOKEN))
                .andExpect(jsonPath("$.expiresIn").value(3600));
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
                .andExpect(status().isNoContent());
    }
} 