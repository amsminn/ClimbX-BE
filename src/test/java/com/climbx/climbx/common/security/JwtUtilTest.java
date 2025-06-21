package com.climbx.climbx.common.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String FIXED_TOKEN = "TEST_FIXED_TOKEN_12345";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(FIXED_TOKEN);
    }

    @Test
    @DisplayName("Authorization 헤더에서 토큰을 정확히 추출한다")
    void shouldExtractTokenFromAuthHeader() {
        // given
        String authHeader = "Bearer " + FIXED_TOKEN;

        // when
        String extractedToken = jwtUtil.extractTokenFromHeader(authHeader);

        // then
        assertThat(extractedToken).isEqualTo(FIXED_TOKEN);
    }

    @Test
    @DisplayName("Bearer로 시작하지 않는 헤더에서는 null을 반환한다")
    void shouldReturnNullForInvalidAuthHeader() {
        // given
        String invalidAuthHeader = "Basic " + FIXED_TOKEN;

        // when
        String extractedToken = jwtUtil.extractTokenFromHeader(invalidAuthHeader);

        // then
        assertThat(extractedToken).isNull();
    }

    @Test
    @DisplayName("null 헤더에서는 null을 반환한다")
    void shouldReturnNullForNullAuthHeader() {
        // when
        String extractedToken = jwtUtil.extractTokenFromHeader(null);

        // then
        assertThat(extractedToken).isNull();
    }

    @Test
    @DisplayName("고정 토큰을 올바르게 반환한다")
    void shouldReturnFixedToken() {
        // when
        String result = jwtUtil.generateFixedToken();

        // then
        assertThat(result).isEqualTo(FIXED_TOKEN);
    }

    @Test
    @DisplayName("유효한 토큰에 대해 true를 반환한다")
    void shouldReturnTrueForValidToken() {
        // when
        boolean isValid = jwtUtil.validateToken(FIXED_TOKEN);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("유효하지 않은 토큰에 대해 false를 반환한다")
    void shouldReturnFalseForInvalidToken() {
        // given
        String invalidToken = "INVALID_TOKEN";

        // when
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("null 토큰에 대해 false를 반환한다")
    void shouldReturnFalseForNullToken() {
        // when
        boolean isValid = jwtUtil.validateToken(null);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("유효한 토큰에서 사용자 ID를 추출한다")
    void shouldExtractSubjectFromValidToken() {
        // when
        BigInteger userId = BigInteger.valueOf(1L);
        BigInteger subject = jwtUtil.extractSubject(FIXED_TOKEN);

        // then
        assertThat(subject).isEqualTo(userId);
    }

    @Test
    @DisplayName("유효하지 않은 토큰에서 null을 반환한다")
    void shouldReturnNullForInvalidTokenSubject() {
        // given
        String invalidToken = "INVALID_TOKEN";

        // when
        BigInteger subject = jwtUtil.extractSubject(invalidToken);

        // then
        assertThat(subject).isNull();
    }

    @Test
    @DisplayName("고정 토큰 값을 올바르게 반환한다")
    void shouldGetFixedToken() {
        // when
        String token = jwtUtil.getFixedToken();

        // then
        assertThat(token).isEqualTo(FIXED_TOKEN);
    }
} 