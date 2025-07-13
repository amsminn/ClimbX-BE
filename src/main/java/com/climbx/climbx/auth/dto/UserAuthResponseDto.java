package com.climbx.climbx.auth.dto;

import com.climbx.climbx.auth.entity.UserAuthEntity;
import lombok.Builder;

/**
 * 현재 사용자 정보 응답 DTO
 */
@Builder
public record UserAuthResponseDto(
    Long id,
    String nickname,
    String email,
    String providerType,
    String providerId,
    boolean isPrimary
) {
    
    public static UserAuthResponseDto from(UserAuthEntity userAuth) {
        return UserAuthResponseDto.builder()
            .id(userAuth.userId())
            .nickname(userAuth.userAccountEntity().nickname())
            .email(userAuth.providerEmail())
            .providerType(userAuth.provider().name())
            .providerId(userAuth.providerId())
            .isPrimary(userAuth.isPrimary())
            .build();
    }
} 