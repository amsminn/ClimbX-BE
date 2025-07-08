package com.climbx.climbx.common.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.anyString;

import com.climbx.climbx.common.enums.RoleType;
import com.climbx.climbx.common.enums.TokenType;
import com.climbx.climbx.common.security.exception.InvalidTokenException;
import com.climbx.climbx.common.security.exception.TokenExpiredException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtContext 테스트")
class JwtContextTest {

    private JwtContext jwtContext;
    
    @Mock
    private HttpServletRequest httpServletRequest;

    private final String testSecret = "test-secret-key-must-be-at-least-256-bits-long-for-HS256-algorithm";
    private final long accessTokenExpiration = 900L; // 15분
    private final long refreshTokenExpiration = 86400L; // 24시간
    private final String issuer = "test-issuer";
    private Key signingKey;

    @BeforeEach
    void setUp() {
        jwtContext = new JwtContext(testSecret, accessTokenExpiration, refreshTokenExpiration, issuer);
        signingKey = Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Nested
    @DisplayName("토큰 생성 테스트")
    class GenerateTokenTest {

        @Test
        @DisplayName("유효한 파라미터로 액세스 토큰을 성공적으로 생성한다")
        void shouldGenerateAccessTokenWithValidParameters() {
            // given
            Long userId = 1L;
            String provider = "KAKAO";
            RoleType role = RoleType.USER;

            // when
            String accessToken = jwtContext.generateAccessToken(userId, provider, role);

            // then
            assertThat(accessToken).isNotNull();
            assertThat(accessToken).isNotEmpty();
            assertThat(jwtContext.extractSubject(accessToken)).isEqualTo(userId);
            assertThat(jwtContext.extractProvider(accessToken)).isEqualTo(provider);
            assertThat(jwtContext.extractRole(accessToken)).isEqualTo(role);
            assertThat(jwtContext.extractTokenType(accessToken)).isEqualTo(TokenType.ACCESS);
        }

        @Test
        @DisplayName("유효한 파라미터로 리프레시 토큰을 성공적으로 생성한다")
        void shouldGenerateRefreshTokenWithValidParameters() {
            // given
            Long userId = 1L;

            // when
            String refreshToken = jwtContext.generateRefreshToken(userId);

            // then
            assertThat(refreshToken).isNotNull();
            assertThat(refreshToken).isNotEmpty();
            assertThat(jwtContext.extractSubject(refreshToken)).isEqualTo(userId);
            assertThat(jwtContext.extractTokenType(refreshToken)).isEqualTo(TokenType.REFRESH);
        }
    }

    @Nested
    @DisplayName("HTTP 요청에서 토큰 추출 테스트")
    class ExtractTokenFromRequestTest {

        @Test
        @DisplayName("유효한 Bearer 토큰을 성공적으로 추출한다")
        void shouldExtractBearerTokenFromRequest() {
            // given
            String token = "valid-bearer-token";
            given(httpServletRequest.getHeader("Authorization")).willReturn("Bearer " + token);

            // when
            String extractedToken = jwtContext.extractTokenFromRequest(httpServletRequest);

            // then
            assertThat(extractedToken).isEqualTo(token);
        }

        @Test
        @DisplayName("Authorization 헤더가 없을 때 예외를 던진다")
        void shouldThrowExceptionWhenAuthorizationHeaderIsNull() {
            // given
            given(httpServletRequest.getHeader("Authorization")).willReturn(null);

            // when & then
            assertThatThrownBy(() -> jwtContext.extractTokenFromRequest(httpServletRequest))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessage("유효하지 않은 토큰입니다.");
        }

        @Test
        @DisplayName("Bearer 토큰 형식이 아닐 때 예외를 던진다")
        void shouldThrowExceptionWhenTokenIsNotBearerFormat() {
            // given
            given(httpServletRequest.getHeader("Authorization")).willReturn("Basic token");

            // when & then
            assertThatThrownBy(() -> jwtContext.extractTokenFromRequest(httpServletRequest))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessage("유효하지 않은 토큰입니다.");
        }
    }

    @Nested
    @DisplayName("토큰에서 사용자 ID 추출 테스트")
    class ExtractSubjectTest {

        @Test
        @DisplayName("유효한 토큰에서 사용자 ID를 성공적으로 추출한다")
        void shouldExtractSubjectFromValidToken() {
            // given
            Long expectedUserId = 123L;
            String token = createValidToken(expectedUserId);

            // when
            Long extractedUserId = jwtContext.extractSubject(token);

            // then
            assertThat(extractedUserId).isEqualTo(expectedUserId);
        }

        @Test
        @DisplayName("null 토큰일 때 예외를 던진다")
        void shouldThrowExceptionWhenTokenIsNull() {
            // when & then
            assertThatThrownBy(() -> jwtContext.extractSubject(null))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessage("유효하지 않은 토큰입니다.");
        }

        @Test
        @DisplayName("만료된 토큰일 때 예외를 던진다")
        void shouldThrowExceptionWhenTokenIsExpired() {
            // given
            String expiredToken = createExpiredToken();

            // when & then
            assertThatThrownBy(() -> jwtContext.extractSubject(expiredToken))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessage("유효하지 않은 토큰입니다.");
        }

        @Test
        @DisplayName("잘못된 형식의 토큰일 때 예외를 던진다")
        void shouldThrowExceptionWhenTokenIsInvalid() {
            // given
            String invalidToken = "invalid-token-format";

            // when & then
            assertThatThrownBy(() -> jwtContext.extractSubject(invalidToken))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessageContaining("유효하지 않은 토큰입니다");
        }
    }

    @Nested
    @DisplayName("토큰에서 토큰 타입 추출 테스트")
    class ExtractTokenTypeTest {

        @Test
        @DisplayName("액세스 토큰에서 토큰 타입을 성공적으로 추출한다")
        void shouldExtractAccessTokenType() {
            // given
            String accessToken = jwtContext.generateAccessToken(1L, "KAKAO", RoleType.USER);

            // when
            TokenType tokenType = jwtContext.extractTokenType(accessToken);

            // then
            assertThat(tokenType).isEqualTo(TokenType.ACCESS);
        }

        @Test
        @DisplayName("리프레시 토큰에서 토큰 타입을 성공적으로 추출한다")
        void shouldExtractRefreshTokenType() {
            // given
            String refreshToken = jwtContext.generateRefreshToken(1L);

            // when
            TokenType tokenType = jwtContext.extractTokenType(refreshToken);

            // then
            assertThat(tokenType).isEqualTo(TokenType.REFRESH);
        }

        @Test
        @DisplayName("토큰 타입이 없는 토큰일 때 예외를 던진다")
        void shouldThrowExceptionWhenTokenTypeIsNotFound() {
            // given
            String tokenWithoutType = createTokenWithoutType();

            // when & then
            assertThatThrownBy(() -> jwtContext.extractTokenType(tokenWithoutType))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessage("유효하지 않은 토큰입니다.");
        }
    }

    @Nested
    @DisplayName("토큰에서 Provider 추출 테스트")
    class ExtractProviderTest {

        @Test
        @DisplayName("유효한 토큰에서 Provider를 성공적으로 추출한다")
        void shouldExtractProviderFromValidToken() {
            // given
            String expectedProvider = "KAKAO";
            String token = jwtContext.generateAccessToken(1L, expectedProvider, RoleType.USER);

            // when
            String extractedProvider = jwtContext.extractProvider(token);

            // then
            assertThat(extractedProvider).isEqualTo(expectedProvider);
        }

        @Test
        @DisplayName("Provider가 없는 토큰일 때 예외를 던진다")
        void shouldThrowExceptionWhenProviderIsNotFound() {
            // given
            String refreshToken = jwtContext.generateRefreshToken(1L);

            // when & then
            assertThatThrownBy(() -> jwtContext.extractProvider(refreshToken))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessage("유효하지 않은 토큰입니다.");
        }
    }

    @Nested
    @DisplayName("토큰에서 Role 추출 테스트")
    class ExtractRoleTest {

        @Test
        @DisplayName("유효한 토큰에서 Role을 성공적으로 추출한다")
        void shouldExtractRoleFromValidToken() {
            // given
            RoleType expectedRole = RoleType.ADMIN;
            String token = jwtContext.generateAccessToken(1L, "KAKAO", expectedRole);

            // when
            RoleType extractedRole = jwtContext.extractRole(token);

            // then
            assertThat(extractedRole).isEqualTo(expectedRole);
        }

        @Test
        @DisplayName("Role이 없는 토큰일 때 예외를 던진다")
        void shouldThrowExceptionWhenRoleIsNotFound() {
            // given
            String refreshToken = jwtContext.generateRefreshToken(1L);

            // when & then
            assertThatThrownBy(() -> jwtContext.extractRole(refreshToken))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessage("유효하지 않은 토큰입니다.");
        }
    }

    @Nested
    @DisplayName("토큰 만료 시간 조회 테스트")
    class GetAccessTokenExpirationTest {

        @Test
        @DisplayName("액세스 토큰 만료 시간을 성공적으로 조회한다")
        void shouldGetAccessTokenExpiration() {
            // when
            Long expiration = jwtContext.getAccessTokenExpiration();

            // then
            assertThat(expiration).isEqualTo(accessTokenExpiration);
        }
    }

    // 테스트용 헬퍼 메서드들
    private String createValidToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 60000); // 1분 후 만료

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("type", TokenType.ACCESS.name())
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private String createExpiredToken() {
        Date now = new Date();
        Date pastDate = new Date(now.getTime() - 60000); // 1분 전 만료

        return Jwts.builder()
                .setSubject("1")
                .setIssuer(issuer)
                .setIssuedAt(pastDate)
                .setExpiration(pastDate)
                .claim("type", TokenType.ACCESS.name())
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private String createTokenWithoutType() {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 60000);

        return Jwts.builder()
                .setSubject("1")
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
} 