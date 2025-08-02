package com.climbx.climbx.user.dto;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record UserProfileModifyRequestDto(

    String newNickname,

    String newStatusMessage,

    MultipartFile profileImage // null 허용
) {

}