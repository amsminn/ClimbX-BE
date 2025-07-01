package com.climbx.climbx.user.dto;

import lombok.Builder;

@Builder
public record UserProfileModifyRequestDto(

    String newNickname,

    String newStatusMessage,

    String newProfileImageUrl // null 허용
) {

}