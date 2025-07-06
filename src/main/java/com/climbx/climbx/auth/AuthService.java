package com.climbx.climbx.auth;

import com.climbx.climbx.auth.dto.LoginResponseDto;
import com.climbx.climbx.auth.dto.OAuth2TokenResponse;
import com.climbx.climbx.auth.dto.OAuth2UserInfo;
import com.climbx.climbx.auth.dto.UserOauth2InfoResponseDto;
import com.climbx.climbx.auth.enums.OAuth2ProviderType;
import com.climbx.climbx.auth.exception.InvalidRefreshTokenException;
import com.climbx.climbx.auth.provider.OAuth2Provider;
import com.climbx.climbx.auth.provider.OAuth2ProviderFactory;
import com.climbx.climbx.common.enums.RoleType;
import com.climbx.climbx.common.security.JwtContext;
import com.climbx.climbx.user.entity.UserAccountEntity;
import com.climbx.climbx.user.entity.UserAuthEntity;
import com.climbx.climbx.user.entity.UserStatEntity;
import com.climbx.climbx.user.exception.UserNotFoundException;
import com.climbx.climbx.user.repository.UserAccountRepository;
import com.climbx.climbx.user.repository.UserAuthRepository;
import com.climbx.climbx.user.repository.UserStatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final JwtContext jwtContext;
    private final UserAccountRepository userAccountRepository;
    private final UserAuthRepository userAuthRepository;
    private final UserStatRepository userStatRepository;
    private final OAuth2ProviderFactory providerFactory;

    /**
     * OAuth2 제공자의 인증 URL을 반환합니다.
     * 모바일 앱에서는 SDK를 통해 처리하므로 실제로는 사용되지 않습니다.
     *
     * @param provider OAuth2 제공자
     * @return 인증 URL (임시 구현)
     */
    public String getAuthorizationUrl(String provider) {
        OAuth2ProviderType providerType = OAuth2ProviderType.fromString(provider);
        // 모바일 앱에서는 각 제공자의 SDK를 사용하므로 서버에서 리다이렉트 URL 제공은 불필요
        return providerType.name().toLowerCase() + "-sdk://oauth";
    }

    /**
     * OAuth2 콜백을 처리하여 사용자 인증을 완료합니다.
     *
     * @param provider OAuth2 제공자
     * @param code 인가 코드
     * @return 로그인 응답 정보
     */
    @Transactional
    public LoginResponseDto handleCallback(String provider, String code) {
        // 1. 적절한 OAuth2Provider 선택
        OAuth2Provider oauth2Provider = providerFactory.getProvider(provider);
        
        // 2. 인가 코드로 액세스 토큰 교환
        OAuth2TokenResponse tokenResponse = oauth2Provider.exchangeCodeForToken(code);
        
        // 3. 액세스 토큰으로 사용자 정보 조회
        OAuth2UserInfo userInfo = oauth2Provider.fetchUserInfo(tokenResponse.accessToken());
        
        // 4. 사용자 정보로 계정 생성 또는 업데이트
        UserAccountEntity user = createOrUpdateUser(userInfo, oauth2Provider.getProviderType());
        
        // 5. JWT 토큰 생성
        String accessToken = jwtContext.generateAccessToken(user.userId(), oauth2Provider.getProviderType().name(), user.role());
        String refreshToken = jwtContext.generateRefreshToken(user.userId());
        
        log.info("사용자 로그인 완료: userId={}, nickname={}, provider={}", 
                user.userId(), user.nickname(), oauth2Provider.getProviderType().name());
        
        return LoginResponseDto.builder()
            .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtContext.getAccessTokenExpiration())
            .build();
    }

    /**
     * 리프레시 토큰으로 새로운 액세스 토큰을 발급합니다.
     *
     * @param refreshToken 리프레시 토큰
     * @return 새로운 로그인 응답 정보
     */
    @Transactional
    public LoginResponseDto refreshAccessToken(String refreshToken) {
        if (!jwtContext.validateToken(refreshToken)) {
            throw new InvalidRefreshTokenException("유효하지 않은 리프레시 토큰입니다.");
        }
        
        String tokenType = jwtContext.getTokenType(refreshToken)
                .orElseThrow(() -> new InvalidRefreshTokenException("토큰 타입을 확인할 수 없습니다."));
        
        if (!"refresh".equals(tokenType)) {
            throw new InvalidRefreshTokenException("리프레시 토큰이 아닙니다.");
        }
        
        Long userId = jwtContext.extractSubject(refreshToken)
                .orElseThrow(() -> new InvalidRefreshTokenException("토큰에서 사용자 정보를 추출할 수 없습니다."));
        
        // 사용자 존재 확인
        UserAccountEntity user = userAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        
        // 기존 토큰에서 provider 정보 추출 (기본값: KAKAO)
        String provider = jwtContext.getProvider(refreshToken).orElse("KAKAO");
        
        // 새로운 토큰 생성
        String newAccessToken = jwtContext.generateAccessToken(userId, provider, user.role());
        String newRefreshToken = jwtContext.generateRefreshToken(userId);
        
        log.info("토큰 갱신 완료: userId={}", userId);
        
        return LoginResponseDto.builder()
            .tokenType("Bearer")
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtContext.getAccessTokenExpiration())
            .build();
    }

    /**
     * 현재 사용자 정보를 반환합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 OAuth2 정보
     */
    public UserOauth2InfoResponseDto getCurrentUserInfo(Long userId) {
        UserAccountEntity user = userAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        
        // 사용자의 주 인증 수단 조회
        String provider = userAuthRepository.findByUserAccountEntity_UserIdAndIsPrimaryTrue(userId)
                .map(userAuth -> userAuth.oauthProvider().name())
                .orElse("UNKNOWN");
        
        return UserOauth2InfoResponseDto.builder()
                .id(user.userId())
                .nickname(user.nickname())
                .provider(provider)
            .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtContext.getAccessTokenExpiration()))
            .build();
    }

    /**
     * 사용자 로그아웃을 처리합니다.
     * 현재는 클라이언트에서 토큰 삭제로 처리됩니다.
     *
     * @param token 액세스 토큰
     */
    public void signOut(String token) {
        // TODO: 향후 토큰 블랙리스트 기능 구현 시 추가
        log.info("사용자 로그아웃 요청");
    }

    /**
     * OAuth2 사용자 정보로 계정을 생성하거나 업데이트합니다.
     * 각 OAuth2 제공자는 독립적인 계정으로 취급됩니다.
     *
     * @param userInfo OAuth2 사용자 정보
     * @param providerType 제공자 타입
     * @return 사용자 계정 엔티티
     */
    private UserAccountEntity createOrUpdateUser(OAuth2UserInfo userInfo, OAuth2ProviderType providerType) {
        String providerId = userInfo.providerId();
        
        // OAuth2 제공자와 제공자 ID로 기존 사용자 인증 정보 찾기
        Optional<UserAuthEntity> existingUserAuth = userAuthRepository
                .findByOauthProviderAndOauthProviderId(providerType, providerId);
        
        if (existingUserAuth.isPresent()) {
            // 기존 사용자 로그인
            UserAuthEntity userAuth = existingUserAuth.get();
            UserAccountEntity user = userAuth.userAccountEntity();
            user.markLogin();
            
            log.info("기존 사용자 로그인: userId={}, nickname={}, provider={}", 
                    user.userId(), user.nickname(), providerType.name());
            
            return userAccountRepository.save(user);
        } else {
            // 새 사용자 생성 (각 제공자별로 독립적인 계정)
            String uniqueNickname = generateUniqueNickname(userInfo, providerType);
            
            // 사용자 계정 생성
            UserAccountEntity newUser = UserAccountEntity.builder()
                    .role(RoleType.USER)
                    .nickname(uniqueNickname)
                    .email(userInfo.email())
                    .statusMessage("ClimbX에 오신 것을 환영합니다!")
                    .profileImageUrl(userInfo.profileImageUrl())
                    .build();
            
            UserAccountEntity savedUser = userAccountRepository.save(newUser);
            
            // OAuth2 인증 정보 생성 (항상 주 인증 수단)
            UserAuthEntity userAuth = UserAuthEntity.builder()
                    .userAccountEntity(savedUser)
                    .oauthProvider(providerType)
                    .oauthProviderId(providerId)
                    .providerEmail(userInfo.email())
                    .isPrimary(true) // 각 계정당 하나의 제공자만 있으므로 항상 주 인증 수단
                    .build();
            
            userAuthRepository.save(userAuth);
            
            // 사용자 통계 엔티티 생성
            UserStatEntity userStat = UserStatEntity.builder()
                    .userId(savedUser.userId())
                    .userAccountEntity(savedUser)
                    .rating(0L)
                    .currentStreak(0L)
                    .longestStreak(0L)
                    .solvedProblemsCount(0L)
                    .rivalCount(0L)
                    .build();
            
            userStatRepository.save(userStat);
            
            log.info("새 사용자 및 인증 정보, 통계 생성 완료: userId={}, nickname={}, provider={}, providerId={}", 
                    savedUser.userId(), savedUser.nickname(), providerType.name(), providerId);
            
            return savedUser;
        }
    }

    /**
     * 고유한 닉네임을 생성합니다.
     * 제공자별로 구분되는 닉네임을 생성합니다.
     *
     * @param userInfo OAuth2 사용자 정보
     * @param providerType OAuth2 제공자 타입
     * @return 고유한 닉네임
     */
    private String generateUniqueNickname(OAuth2UserInfo userInfo, OAuth2ProviderType providerType) {
        String baseNickname = userInfo.getDisplayName();
        
        if (baseNickname == null || baseNickname.trim().isEmpty()) {
            baseNickname = "클라이머";
        }
        
        // 제공자 정보를 포함한 기본 닉네임 생성
        String providerPrefix = providerType.name().toLowerCase();
        String nicknameWithProvider = baseNickname + "_" + providerPrefix;
        
        // 중복 검사 및 고유 닉네임 생성
        String uniqueNickname = nicknameWithProvider;
        int suffix = 1;
        
        while (userAccountRepository.existsByNickname(uniqueNickname)) {
            uniqueNickname = nicknameWithProvider + suffix;
            suffix++;
        }
        
        return uniqueNickname;
    }
}