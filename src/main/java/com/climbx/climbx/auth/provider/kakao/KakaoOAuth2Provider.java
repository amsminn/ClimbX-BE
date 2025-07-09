package com.climbx.climbx.auth.provider.kakao;

import com.climbx.climbx.auth.dto.OAuth2TokenResponseDto;
import com.climbx.climbx.auth.dto.OAuth2UserInfoDto;
import com.climbx.climbx.auth.enums.OAuth2ProviderType;
import com.climbx.climbx.auth.exception.OAuth2TokenExchangeFailedException;
import com.climbx.climbx.auth.exception.OAuth2UserInfoFetchFailedException;
import com.climbx.climbx.auth.provider.OAuth2Provider;
import com.climbx.climbx.auth.provider.kakao.dto.KakaoTokenResponseDto;
import com.climbx.climbx.auth.provider.kakao.dto.KakaoUserInfoResponseDto;
import com.climbx.climbx.auth.provider.kakao.dto.KakaoUserInfoResponseDto.KakaoAccount;
import com.climbx.climbx.auth.provider.kakao.dto.KakaoUserInfoResponseDto.Profile;
import java.util.Optional;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoOAuth2Provider implements OAuth2Provider {

    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.kakao.client.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.kakao.client.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.kakao.client.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.kakao.provider.token-uri}")
    private String tokenUri;

    @Value("${spring.security.oauth2.kakao.provider.user-info-uri}")
    private String userInfoUri;

    @Override
    public OAuth2TokenResponseDto exchangeCodeForToken(String code) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            // OAuth2 스펙 상 APPLICATION_FORM_URLENCODED 포맷으로 파라미터를 받도록 되어있음

            HttpEntity<MultiValueMap<String, String>> request = getMultiValueMapHttpEntity(
                code, headers);

            ResponseEntity<KakaoTokenResponseDto> response = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                request,
                KakaoTokenResponseDto.class
            );

            KakaoTokenResponseDto kakaoToken = response.getBody();
            if (kakaoToken == null) {
                throw new OAuth2TokenExchangeFailedException("카카오 토큰 응답이 비어있습니다.");
            }

            log.info("카카오 토큰 교환 성공");

            return OAuth2TokenResponseDto.builder()
                .accessToken(kakaoToken.accessToken())
                .refreshToken(kakaoToken.refreshToken())
                .tokenType(kakaoToken.tokenType())
                .expiresIn(
                    kakaoToken.expiresIn() != null
                        ? kakaoToken.expiresIn().longValue()
                        : null
                )
                .scope(kakaoToken.scope())
                .idToken(kakaoToken.idToken())
                .build();

        } catch (Exception e) {
            log.error("카카오 토큰 교환 실패: {}", e.getMessage());
            throw new OAuth2TokenExchangeFailedException("카카오 토큰 교환에 실패했습니다: " + e.getMessage());
        }
    }

    private HttpEntity<MultiValueMap<String, String>> getMultiValueMapHttpEntity(String code,
        HttpHeaders headers) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        // FormHttpMessageConverter가 MultiValueMap 사용 시 자동으로 쿼리 파라미터 형식으로 변환해줌
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri); // 코드를 받았던 리다이렉트 콜백 URL
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return request;
    }

    @Override
    public OAuth2UserInfoDto fetchUserInfo(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<KakaoUserInfoResponseDto> response = restTemplate.exchange(
                userInfoUri,
                HttpMethod.GET,
                request,
                KakaoUserInfoResponseDto.class
            );

            KakaoUserInfoResponseDto kakaoUser = response.getBody();
            if (kakaoUser == null) {
                throw new OAuth2UserInfoFetchFailedException("카카오 사용자 정보 응답이 비어있습니다.");
            }

            log.info("카카오 사용자 정보 조회 성공: id={}", kakaoUser.id());

            return OAuth2UserInfoDto.builder()
                .providerId(kakaoUser.id().toString())
                .email(extractEmail(kakaoUser))
                .nickname(extractNickname(kakaoUser))
                .name(extractNickname(kakaoUser)) // 카카오에서는 nickname을 name으로 사용
                .profileImageUrl(extractProfileImageUrl(kakaoUser))
                .emailVerified(isEmailVerified(kakaoUser))
                .build();

        } catch (Exception e) {
            log.error("카카오 사용자 정보 조회 실패: {}", e.getMessage());
            throw new OAuth2UserInfoFetchFailedException(
                "카카오 사용자 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public OAuth2ProviderType getProviderType() {
        return OAuth2ProviderType.KAKAO;
    }

    private String extractEmail(KakaoUserInfoResponseDto userInfo) {
        return Optional.ofNullable(userInfo.kakaoAccount())
            .map(KakaoAccount::email)
            .orElse(null);
    }

    private String extractNickname(KakaoUserInfoResponseDto userInfo) {
        return Optional.ofNullable(userInfo.kakaoAccount())
            .map(KakaoAccount::profile)
            .map(Profile::nickname)
            .orElse(null);
    }

    private String extractProfileImageUrl(KakaoUserInfoResponseDto userInfo) {
        return Optional.ofNullable(userInfo.kakaoAccount())
            .map(KakaoAccount::profile)
            .map(Profile::profileImageUrl)
            .orElse(null);
    }

    private boolean isEmailVerified(KakaoUserInfoResponseDto userInfo) {
        return Optional.ofNullable(userInfo.kakaoAccount())
            .map(KakaoAccount::isEmailVerified)
            .orElse(false);
    }
} 