package com.climbx.climbx.auth.provider;

import com.climbx.climbx.auth.dto.OAuth2TokenResponse;
import com.climbx.climbx.auth.dto.OAuth2UserInfo;
import com.climbx.climbx.auth.dto.provider.kakao.KakaoTokenResponseDto;
import com.climbx.climbx.auth.dto.provider.kakao.KakaoUserInfoResponseDto;
import com.climbx.climbx.auth.enums.OAuth2ProviderType;
import com.climbx.climbx.auth.exception.OAuth2TokenExchangeFailedException;
import com.climbx.climbx.auth.exception.OAuth2UserInfoFetchFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoOAuth2Provider implements OAuth2Provider {
    
    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    
    private final RestTemplate restTemplate;
    
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;
    
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;
    
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    @Override
    public OAuth2TokenResponse exchangeCodeForToken(String code) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", redirectUri);
            params.add("code", code);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            
            ResponseEntity<KakaoTokenResponseDto> response = restTemplate.exchange(
                    KAKAO_TOKEN_URL,
                    HttpMethod.POST,
                    request,
                    KakaoTokenResponseDto.class
            );
            
            KakaoTokenResponseDto kakaoToken = response.getBody();
            if (kakaoToken == null) {
                throw new OAuth2TokenExchangeFailedException("카카오 토큰 응답이 비어있습니다.");
            }
            
            log.info("카카오 토큰 교환 성공");
            
            return OAuth2TokenResponse.builder()
                    .accessToken(kakaoToken.accessToken())
                    .refreshToken(kakaoToken.refreshToken())
                    .tokenType(kakaoToken.tokenType())
                    .expiresIn(kakaoToken.expiresIn() != null ? kakaoToken.expiresIn().longValue() : null)
                    .scope(kakaoToken.scope())
                    .idToken(kakaoToken.idToken())
                    .build();
                    
        } catch (Exception e) {
            log.error("카카오 토큰 교환 실패: {}", e.getMessage());
            throw new OAuth2TokenExchangeFailedException("카카오 토큰 교환에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public OAuth2UserInfo fetchUserInfo(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            
            ResponseEntity<KakaoUserInfoResponseDto> response = restTemplate.exchange(
                    KAKAO_USER_INFO_URL,
                    HttpMethod.GET,
                    request,
                    KakaoUserInfoResponseDto.class
            );
            
            KakaoUserInfoResponseDto kakaoUser = response.getBody();
            if (kakaoUser == null) {
                throw new OAuth2UserInfoFetchFailedException("카카오 사용자 정보 응답이 비어있습니다.");
            }
            
            log.info("카카오 사용자 정보 조회 성공: id={}", kakaoUser.id());
            
            return OAuth2UserInfo.builder()
                    .providerId(kakaoUser.id().toString())
                    .email(extractEmail(kakaoUser))
                    .nickname(extractNickname(kakaoUser))
                    .profileImageUrl(extractProfileImageUrl(kakaoUser))
                    .emailVerified(isEmailVerified(kakaoUser))
                    .build();
                    
        } catch (Exception e) {
            log.error("카카오 사용자 정보 조회 실패: {}", e.getMessage());
            throw new OAuth2UserInfoFetchFailedException("카카오 사용자 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public OAuth2ProviderType getProviderType() {
        return OAuth2ProviderType.KAKAO;
    }
    
    private String extractEmail(KakaoUserInfoResponseDto userInfo) {
        return Optional.ofNullable(userInfo.kakaoAccount())
                .map(account -> account.email())
                .orElse(null);
    }
    
    private String extractNickname(KakaoUserInfoResponseDto userInfo) {
        return Optional.ofNullable(userInfo.kakaoAccount())
                .map(account -> account.profile())
                .map(profile -> profile.nickname())
                .orElse(null);
    }
    
    private String extractProfileImageUrl(KakaoUserInfoResponseDto userInfo) {
        return Optional.ofNullable(userInfo.kakaoAccount())
                .map(account -> account.profile())
                .map(profile -> profile.profileImageUrl())
                .orElse(null);
    }
    
    private boolean isEmailVerified(KakaoUserInfoResponseDto userInfo) {
        return Optional.ofNullable(userInfo.kakaoAccount())
                .map(account -> account.isEmailVerified())
                .orElse(false);
    }
} 