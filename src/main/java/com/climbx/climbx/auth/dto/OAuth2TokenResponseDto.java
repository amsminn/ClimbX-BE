package com.climbx.climbx.auth.dto;

import lombok.Builder;

@Builder
<<<<<<< HEAD:src/main/java/com/climbx/climbx/auth/dto/OAuth2TokenResponseDto.java
public record OAuth2TokenResponseDto(
<<<<<<< HEAD
=======
=======
public record OAuth2TokenResponse(
>>>>>>> 8947ec5 (refactor: 인증 관련 DTO, 예외 처리, JWT 필터 및 테스트 코드 리팩토링):src/main/java/com/climbx/climbx/auth/dto/OAuth2TokenResponse.java

>>>>>>> 4d7347d (refactor: 인증 관련 DTO, 예외 처리, JWT 필터 및 테스트 코드 리팩토링)
    String accessToken,
    String refreshToken,
    String tokenType,
    Long expiresIn,
    String scope,
    String idToken // OpenID Connect용 (Apple, Google)
) {

}