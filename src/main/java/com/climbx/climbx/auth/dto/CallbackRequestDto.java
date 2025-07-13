package com.climbx.climbx.auth.dto;

import lombok.Builder;

@Builder
public record CallbackRequestDto(

    String idToken, // OpenID 토큰
    String nonce // replay attack 방지를 위한 nonce 값
) {

}