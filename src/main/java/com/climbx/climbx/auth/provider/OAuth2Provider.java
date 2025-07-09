package com.climbx.climbx.auth.provider;

import com.climbx.climbx.auth.dto.OAuth2TokenResponseDto;
import com.climbx.climbx.auth.dto.OAuth2UserInfoDto;
import com.climbx.climbx.auth.enums.OAuth2ProviderType;

public interface OAuth2Provider {

    /**
     * 인가 코드를 액세스 토큰으로 교환합니다.
     *
     * @param code 인가 코드
     * @return 토큰 응답
     */
    OAuth2TokenResponseDto exchangeCodeForToken(String code);

    /**
     * 액세스 토큰으로 사용자 정보를 조회합니다.
     *
     * @param accessToken 액세스 토큰
     * @return 사용자 정보
     */
    OAuth2UserInfoDto fetchUserInfo(String accessToken);

    /**
     * 제공자 타입을 반환합니다.
     *
     * @return 제공자 타입
     */
    OAuth2ProviderType getProviderType();
} 