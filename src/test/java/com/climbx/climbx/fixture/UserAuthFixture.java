package com.climbx.climbx.fixture;

import com.climbx.climbx.auth.entity.UserAuthEntity;
import com.climbx.climbx.auth.enums.OAuth2ProviderType;
import com.climbx.climbx.user.entity.UserAccountEntity;

public class UserAuthFixture {

    public static final String DEFAULT_PROVIDER_ID = "12345";
    public static final String DEFAULT_PROVIDER_EMAIL = "test@example.com";
    public static final OAuth2ProviderType DEFAULT_PROVIDER_TYPE = OAuth2ProviderType.KAKAO;
    public static final boolean DEFAULT_IS_PRIMARY = true;

    // 기본 UserAuthEntity 생성
    public static UserAuthEntity createUserAuth(UserAccountEntity userAccount) {
        return createUserAuth(
            userAccount,
            DEFAULT_PROVIDER_TYPE,
            DEFAULT_PROVIDER_ID,
            DEFAULT_PROVIDER_EMAIL,
            DEFAULT_IS_PRIMARY
        );
    }

    // 제공자 타입과 함께 UserAuthEntity 생성
    public static UserAuthEntity createUserAuth(
        UserAccountEntity userAccount,
        OAuth2ProviderType providerType
    ) {
        return createUserAuth(
            userAccount,
            providerType,
            DEFAULT_PROVIDER_ID,
            DEFAULT_PROVIDER_EMAIL,
            DEFAULT_IS_PRIMARY
        );
    }

    // 제공자 ID와 함께 UserAuthEntity 생성
    public static UserAuthEntity createUserAuth(
        UserAccountEntity userAccount,
        String providerId
    ) {
        return createUserAuth(
            userAccount,
            DEFAULT_PROVIDER_TYPE,
            providerId,
            DEFAULT_PROVIDER_EMAIL,
            DEFAULT_IS_PRIMARY
        );
    }

    // 주 인증 여부와 함께 UserAuthEntity 생성
    public static UserAuthEntity createUserAuth(
        UserAccountEntity userAccount,
        boolean isPrimary
    ) {
        return createUserAuth(
            userAccount,
            DEFAULT_PROVIDER_TYPE,
            DEFAULT_PROVIDER_ID,
            DEFAULT_PROVIDER_EMAIL,
            isPrimary
        );
    }

    // 제공자 타입과 주 인증 여부와 함께 UserAuthEntity 생성
    public static UserAuthEntity createUserAuth(
        UserAccountEntity userAccount,
        OAuth2ProviderType providerType,
        boolean isPrimary
    ) {
        return createUserAuth(
            userAccount,
            providerType,
            DEFAULT_PROVIDER_ID,
            DEFAULT_PROVIDER_EMAIL,
            isPrimary
        );
    }

    // 모든 필드를 지정하여 UserAuthEntity 생성
    public static UserAuthEntity createUserAuth(
        UserAccountEntity userAccount,
        OAuth2ProviderType providerType,
        String providerId,
        String providerEmail,
        boolean isPrimary
    ) {
        return UserAuthEntity.builder()
            .userAccountEntity(userAccount)
            .provider(providerType)
            .providerId(providerId)
            .providerEmail(providerEmail)
            .isPrimary(isPrimary)
            .build();
    }

    // 카카오 인증 정보 생성 (편의 메서드)
    public static UserAuthEntity createKakaoAuth(UserAccountEntity userAccount) {
        return createUserAuth(userAccount, OAuth2ProviderType.KAKAO);
    }

    // 카카오 인증 정보 생성 with 제공자 ID
    public static UserAuthEntity createKakaoAuth(
        UserAccountEntity userAccount,
        String providerId
    ) {
        return createUserAuth(
            userAccount,
            OAuth2ProviderType.KAKAO,
            providerId,
            DEFAULT_PROVIDER_EMAIL,
            DEFAULT_IS_PRIMARY
        );
    }

    // 주 인증 수단이 아닌 카카오 인증 정보 생성
    public static UserAuthEntity createSecondaryKakaoAuth(UserAccountEntity userAccount) {
        return createUserAuth(userAccount, OAuth2ProviderType.KAKAO, false);
    }
} 