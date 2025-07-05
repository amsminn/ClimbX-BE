package com.climbx.climbx.auth.provider;

import com.climbx.climbx.auth.enums.OAuth2ProviderType;
import com.climbx.climbx.auth.exception.OAuth2ProviderNotSupportedException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OAuth2ProviderFactory {
    
    private final Map<OAuth2ProviderType, OAuth2Provider> providers;
    
    public OAuth2ProviderFactory(List<OAuth2Provider> providerList) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(
                        OAuth2Provider::getProviderType,
                        Function.identity()
                ));
    }
    
    /**
     * providerType에 따라 구현체 반환
     */
    public OAuth2Provider getProvider(OAuth2ProviderType providerType) {
        OAuth2Provider provider = providers.get(providerType);
        if (provider == null) {
            throw new OAuth2ProviderNotSupportedException(providerType.name());
        }
        return provider;
    }
    
    /**
     * 문자열로부터 OAuth2Provider를 반환합니다.
     *
     * @param providerName 제공자 이름 (대소문자 구분 없음)
     * @return OAuth2Provider 구현체
     * @throws OAuth2ProviderNotSupportedException 지원하지 않는 제공자인 경우
     */
    public OAuth2Provider getProvider(String providerName) {
        OAuth2ProviderType providerType = OAuth2ProviderType.fromString(providerName);
        return getProvider(providerType);
    }
} 