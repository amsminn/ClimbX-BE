package com.climbx.climbx.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record UserProfileModifyRequestDto(
    @Size(min = 1, max = 64, message = "닉네임은 최대 64자까지 입력 가능합니다.")
    String newNickname,

    @Size(max = 128, message = "상태 메시지는 최대 128 입력 가능합니다.")
    String newStatusMessage,

    MultipartFile profileImage // null 허용
) {

}