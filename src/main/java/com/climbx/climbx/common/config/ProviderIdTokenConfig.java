package com.climbx.climbx.common.config;

import com.climbx.climbx.auth.provider.UserInfoExtractor;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * OAuth2 ID Token 검증을 위한 Configuration Spring Security OAuth2 Resource Server 기반
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ProviderIdTokenConfig {

    private final List<UserInfoExtractor> userInfoExtractors;

    /**
     * Provider별 JwtDecoder 맵을 생성합니다. 각 Provider의 구현체에서 제공하는 설정을 사용하여 JwtDecoder를 동적으로 생성합니다.
     */
    @Bean
    public Map<String, JwtDecoder> oauth2IdTokenDecoders() {
        log.info("OAuth2 ID Token Decoders 초기화 시작");

        Map<String, JwtDecoder> decoders = userInfoExtractors.stream()
            .collect(Collectors.toMap(
                extractor -> extractor.getProviderType().name().toLowerCase(),
                this::createJwtDecoder
            ));

        log.info("OAuth2 ID Token Decoders 초기화 완료: {}", decoders.keySet());
        return decoders;
    }

    /**
     * UserInfoExtractor에서 제공하는 설정을 사용하여 JwtDecoder를 생성합니다. NimbusJwtDecoder가 공개키 캐싱 및 자동 갱신 기능 제공
     */
    private JwtDecoder createJwtDecoder(UserInfoExtractor extractor) {
        String providerType = extractor.getProviderType().name();
        String jwksUri = extractor.getJwksUri();
        String issuer = extractor.getIssuer();

        log.debug("{} JwtDecoder 생성 시작: jwksUri={}, issuer={}", providerType, jwksUri, issuer);

        // NimbusJwtDecoder를 직접 생성하여 커스텀 검증기 설정 가능
        NimbusJwtDecoder decoder = NimbusJwtDecoder
            .withJwkSetUri(jwksUri)
            .jwsAlgorithm(SignatureAlgorithm.RS256)
            .build();

        // 표준 검증기 설정 (issuer 검증 포함)
        OAuth2TokenValidator<Jwt> defaultValidators = JwtValidators.createDefaultWithIssuer(issuer);

        // audience 검증을 위한 커스텀 검증기
        OAuth2TokenValidator<Jwt> audienceValidator = createAudienceValidator(
            extractor.getClientId());

        // 모든 검증기를 결합
        OAuth2TokenValidator<Jwt> combinedValidator = new DelegatingOAuth2TokenValidator<>(
            defaultValidators,
            audienceValidator
        );

        decoder.setJwtValidator(combinedValidator);

        log.info("{} JwtDecoder 생성 완료", providerType);
        return decoder;
    }

    /**
     * Audience 검증을 위한 커스텀 Validator 생성 재사용 가능한 검증기입니다.
     */
    private OAuth2TokenValidator<Jwt> createAudienceValidator(String expectedAudience) {
        return new OAuth2TokenValidator<Jwt>() {
            @Override
            public OAuth2TokenValidatorResult validate(Jwt jwt) {
                List<String> audiences = jwt.getAudience();

                if (audiences != null && audiences.contains(expectedAudience)) {
                    return OAuth2TokenValidatorResult.success();
                }

                return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error(
                        "invalid_audience",
                        "The required audience is missing. Expected: " + expectedAudience,
                        null
                    )
                );
            }
        };
    }
} 