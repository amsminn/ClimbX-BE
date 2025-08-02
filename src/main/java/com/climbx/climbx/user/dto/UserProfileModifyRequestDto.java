package com.climbx.climbx.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record UserProfileModifyRequestDto(
    @Size(min = 1, max = 64)
    String newNickname,

    @Size(max = 128)
    String newStatusMessage,

    MultipartFile profileImage // null 허용
) {

}