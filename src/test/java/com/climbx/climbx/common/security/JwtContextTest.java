//package com.climbx.climbx.common.security;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.util.Optional;
//
//@DisplayName("JwtContext 테스트")
//class JwtContextTest {
//
//    private final String FIXED_TOKEN = "TEST_FIXED_TOKEN";
//    private JwtContext jwtContext;
//
//    @BeforeEach
//    void setUp() {
//        // JWT 설정값들을 테스트용으로 설정
//        jwtContext = new JwtContext(
//            "test-secret-key-for-jwt-minimum-256-bits", // secret (최소 256bit)
//            3600L,     // accessTokenExpiration
//            1209600L,  // refreshTokenExpiration
//            "test-issuer" // issuer
//        );
//    }
//
//    @Test
//    @DisplayName("Authorization 헤더에서 토큰을 정확히 추출한다")
//    void shouldExtractTokenFromAuthHeader() {
//        // given
//        String authHeader = "Bearer " + FIXED_TOKEN;
//
//        // when
//        Optional<String> extractedToken = jwtContext.extractTokenFromHeader(authHeader);
//
//        // then
//        assertThat(extractedToken).isPresent();
//        assertThat(extractedToken.get()).isEqualTo(FIXED_TOKEN);
//    }
//
//    @Test
//    @DisplayName("Bearer로 시작하지 않는 헤더에서는 Optional.empty()를 반환한다")
//    void shouldReturnEmptyForInvalidAuthHeader() {
//        // given
//        String invalidAuthHeader = "Basic " + FIXED_TOKEN;
//
//        // when
//        Optional<String> extractedToken = jwtContext.extractTokenFromHeader(invalidAuthHeader);
//
//        // then
//        assertThat(extractedToken).isEmpty();
//    }
//
//    @Test
//    @DisplayName("null 헤더에서는 Optional.empty()를 반환한다")
//    void shouldReturnEmptyForNullAuthHeader() {
//        // when
//        Optional<String> extractedToken = jwtContext.extractTokenFromHeader(null);
//
//        // then
//        assertThat(extractedToken).isEmpty();
//    }
//
//    @Test
//    @DisplayName("실제 JWT 토큰을 생성하고 검증한다")
//    void shouldGenerateAndValidateRealJwtToken() {
//        // given
//        Long userId = 123L;
//        String provider = "KAKAO";
//
//        // when
//        String accessToken = jwtContext.generateAccessToken(userId, provider);
//
//        // then
//        assertThat(jwtContext.validateToken(accessToken)).isTrue();
//
//        Optional<Long> extractedUserId = jwtContext.extractSubject(accessToken);
//        assertThat(extractedUserId).isPresent();
//        assertThat(extractedUserId.get()).isEqualTo(userId);
//
//        Optional<String> tokenType = jwtContext.getTokenType(accessToken);
//        assertThat(tokenType).isPresent();
//        assertThat(tokenType.get()).isEqualTo("access");
//
//        Optional<String> extractedProvider = jwtContext.getProvider(accessToken);
//        assertThat(extractedProvider).isPresent();
//        assertThat(extractedProvider.get()).isEqualTo(provider);
//    }
//
//    @Test
//    @DisplayName("유효하지 않은 토큰에서 Optional.empty()를 반환한다")
//    void shouldReturnEmptyForInvalidToken() {
//        // given
//        String invalidToken = "INVALID_TOKEN";
//
//        // when
//        Optional<Long> subject = jwtContext.extractSubject(invalidToken);
//        Optional<String> tokenType = jwtContext.getTokenType(invalidToken);
//
//        // then
//        assertThat(subject).isEmpty();
//        assertThat(tokenType).isEmpty();
//    }
//
//    @Test
//    @DisplayName("null 토큰에 대해 false를 반환한다")
//    void shouldReturnFalseForNullToken() {
//        // when
//        boolean isValid = jwtContext.validateToken(null);
//
//        // then
//        assertThat(isValid).isFalse();
//    }
//
//    @Test
//    @DisplayName("리프레시 토큰을 생성하고 검증한다")
//    void shouldGenerateAndValidateRefreshToken() {
//        // given
//        Long userId = 456L;
//
//        // when
//        String refreshToken = jwtContext.generateRefreshToken(userId);
//
//        // then
//        assertThat(jwtContext.validateToken(refreshToken)).isTrue();
//
//        Optional<Long> extractedUserId = jwtContext.extractSubject(refreshToken);
//        assertThat(extractedUserId).isPresent();
//        assertThat(extractedUserId.get()).isEqualTo(userId);
//
//        Optional<String> tokenType = jwtContext.getTokenType(refreshToken);
//        assertThat(tokenType).isPresent();
//        assertThat(tokenType.get()).isEqualTo("refresh");
//    }
//
//    @Test
//    @DisplayName("고정 토큰을 올바르게 반환한다 (하위 호환성)")
//    void shouldReturnFixedToken() {
//        // when
//        String result = jwtContext.generateFixedToken();
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(jwtContext.validateToken(result)).isTrue();
//    }
//
//    @Test
//    @DisplayName("고정 토큰 값을 올바르게 반환한다 (하위 호환성)")
//    void shouldGetFixedToken() {
//        // when
//        String token = jwtContext.getFixedToken();
//
//        // then
//        assertThat(token).isNotNull();
//        assertThat(jwtContext.validateToken(token)).isTrue();
//    }
//}