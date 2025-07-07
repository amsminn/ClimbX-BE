package com.climbx.climbx.auth.dto;

import lombok.Builder;

@Builder
<<<<<<< HEAD:src/main/java/com/climbx/climbx/auth/dto/OAuth2UserInfoDto.java
public record OAuth2UserInfoDto(
=======
public record OAuth2UserInfo(
>>>>>>> 8947ec5 (refactor: 인증 관련 DTO, 예외 처리, JWT 필터 및 테스트 코드 리팩토링):src/main/java/com/climbx/climbx/auth/dto/OAuth2UserInfo.java

    String providerId,     // 제공자별 고유 ID
    String email,
    String nickname,
    String name,
    String profileImageUrl, // 프로필 이미지 URL
    Boolean emailVerified   // 이메일 검증 여부
) {

    public String getDisplayName() {
        return nickname != null ? nickname : name;
    }
} 