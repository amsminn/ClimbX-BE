package com.climbx.climbx.auth.provider.kakao;

import com.climbx.climbx.auth.dto.OAuth2TokenResponseDto;
import com.climbx.climbx.auth.dto.OAuth2UserInfoDto;
import com.climbx.climbx.auth.enums.OAuth2ProviderType;
import com.climbx.climbx.auth.exception.ProviderTokenExchangeFailedException;
import com.climbx.climbx.auth.exception.ProviderTokenExpiredException;
import com.climbx.climbx.auth.exception.ProviderUserInfoFetchFailedException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
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
                throw new ProviderUserInfoFetchFailedException(OAuth2ProviderType.KAKAO);
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
            log.error("카카오 토큰 교환 중 예기치 못한 오류 발생: {}", e.getMessage(), e);
            throw new ProviderTokenExchangeFailedException(OAuth2ProviderType.KAKAO);
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

            KakaoUserInfoResponseDto kakaoUser = Optional.ofNullable(response.getBody())
                .orElseThrow(
                    () -> new ProviderUserInfoFetchFailedException(OAuth2ProviderType.KAKAO));

            log.info("카카오 사용자 정보 조회 성공: id={}", kakaoUser.id());

            return OAuth2UserInfoDto.builder()
                .providerId(kakaoUser.id().toString())
                .nickname(KakaoUserInfoResponseDto.getNickname(kakaoUser))
                .profileImageUrl(KakaoUserInfoResponseDto.getProfileImageUrl(kakaoUser))
                .email(KakaoUserInfoResponseDto.getEmail(kakaoUser))
                .emailVerified(KakaoUserInfoResponseDto.isEmailVerified(kakaoUser))
                .build();

        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                log.error(
                    "카카오 사용자 정보 조회 실패 - 인증 실패 (401): HTTP Status: {}, Response Body: {}, URL: {}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString(),
                    userInfoUri
                );
                throw new ProviderTokenExpiredException(OAuth2ProviderType.KAKAO);
            } else {
                log.error(
                    "카카오 사용자 정보 조회 실패 - 기타 에러: HTTP Status: {}, Response Body: {}, URL: {}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString(),
                    userInfoUri
                );
                throw new ProviderUserInfoFetchFailedException(OAuth2ProviderType.KAKAO);
            }
        } catch (Exception e) {
            log.error("카카오 사용자 정보 조회 중 예기치 못한 오류 발생: {}", e.getMessage(), e);
            throw new ProviderUserInfoFetchFailedException(OAuth2ProviderType.KAKAO);
        }
    }

    @Override
    public OAuth2ProviderType getProviderType() {
        return OAuth2ProviderType.KAKAO;
    }
} 