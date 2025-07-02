package com.climbx.climbx.common.filter;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.climbx.climbx.common.security.JwtAuthenticationFilter;
import com.climbx.climbx.common.security.JwtContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private final String VALID_TOKEN = "VALID_TEST_TOKEN";
    @Mock
    private JwtContext jwtUtil;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Mock
    private SecurityContext securityContext;
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("유효한 토큰으로 요청 시 인증이 설정된다")
    void shouldSetAuthenticationWhenValidToken() throws ServletException, IOException {
        // given
        String authHeader = "Bearer " + VALID_TOKEN;
        given(request.getHeader("Authorization")).willReturn(authHeader);
        given(jwtUtil.validateToken(VALID_TOKEN)).willReturn(true);
        given(jwtUtil.extractSubject(VALID_TOKEN)).willReturn(1L);
        given(jwtUtil.extractTokenFromHeader(authHeader)).willReturn(VALID_TOKEN);
        given(securityContext.getAuthentication()).willReturn(null);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(securityContext).setAuthentication(org.mockito.ArgumentMatchers.any());
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil).extractTokenFromHeader(authHeader);
    }

    @Test
    @DisplayName("Authorization 헤더가 없으면 인증을 설정하지 않는다")
    void shouldNotSetAuthenticationWhenNoAuthHeader() throws ServletException, IOException {
        // given
        given(request.getHeader("Authorization")).willReturn(null);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(securityContext, never()).setAuthentication(org.mockito.ArgumentMatchers.any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Bearer로 시작하지 않는 헤더는 인증을 설정하지 않는다")
    void shouldNotSetAuthenticationWhenNotBearerToken() throws ServletException, IOException {
        // given
        given(request.getHeader("Authorization")).willReturn("Basic token");

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(securityContext, never()).setAuthentication(org.mockito.ArgumentMatchers.any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 요청 시 인증을 설정하지 않는다")
    void shouldNotSetAuthenticationWhenInvalidToken() throws ServletException, IOException {
        // given
        String authHeader = "Bearer invalid_token";
        given(request.getHeader("Authorization")).willReturn(authHeader);
        given(jwtUtil.validateToken("invalid_token")).willReturn(false);
        given(jwtUtil.extractTokenFromHeader(authHeader)).willReturn("invalid_token");

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(securityContext, never()).setAuthentication(org.mockito.ArgumentMatchers.any());
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil).extractTokenFromHeader(authHeader);
    }

    @Test
    @DisplayName("이미 인증이 설정된 경우 재설정하지 않는다")
    void shouldNotSetAuthenticationWhenAlreadyAuthenticated() throws ServletException, IOException {
        // given
        String authHeader = "Bearer " + VALID_TOKEN;
        Authentication existingAuth = org.mockito.Mockito.mock(Authentication.class);
        given(request.getHeader("Authorization")).willReturn(authHeader);
        given(jwtUtil.validateToken(VALID_TOKEN)).willReturn(true);
        given(jwtUtil.extractTokenFromHeader(authHeader)).willReturn(VALID_TOKEN);
        given(securityContext.getAuthentication()).willReturn(existingAuth);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(securityContext, never()).setAuthentication(org.mockito.ArgumentMatchers.any());
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil).extractTokenFromHeader(authHeader);
    }

    @Test
    @DisplayName("토큰에서 subject를 추출할 수 없으면 인증을 설정하지 않는다")
    void shouldNotSetAuthenticationWhenSubjectIsNull() throws ServletException, IOException {
        // given
        String authHeader = "Bearer " + VALID_TOKEN;
        given(request.getHeader("Authorization")).willReturn(authHeader);
        given(jwtUtil.validateToken(VALID_TOKEN)).willReturn(true);
        given(jwtUtil.extractSubject(VALID_TOKEN)).willReturn(null);
        given(jwtUtil.extractTokenFromHeader(authHeader)).willReturn(VALID_TOKEN);
        given(securityContext.getAuthentication()).willReturn(null);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(securityContext, never()).setAuthentication(org.mockito.ArgumentMatchers.any());
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil).extractTokenFromHeader(authHeader);
    }

    @Test
    @DisplayName("모든 경우에 필터 체인이 계속 진행된다")
    void shouldAlwaysContinueFilterChain() throws ServletException, IOException {
        // given
        given(request.getHeader("Authorization")).willReturn(null);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
    }
} 