package com.climbx.climbx.common.dto;

import com.climbx.climbx.common.util.TimeContext;
import java.time.Instant;
import lombok.Builder;

@Builder
public record ApiResponseDto<T>(

    Long httpStatus,

    String statusMessage,

    Instant timeStamp,

    Long responseTimeMs,

    String path,

    T data
) {

    public static <T> ApiResponseDto<T> success(T data) {
        /*
         * success
         */
        return ApiResponseDto.<T>builder()
            .httpStatus(200L)
            .statusMessage("SUCCESS")
            .timeStamp(Instant.now())
            .responseTimeMs(TimeContext.getResponseTime())
            .path(TimeContext.getPath())
            .data(data)
            .build();
    }

    public static <T> ApiResponseDto<T> success(T data, String message) {
        /*
         * success with custom message
         */
        return ApiResponseDto.<T>builder()
            .httpStatus(200L)
            .statusMessage(message)
            .timeStamp(Instant.now())
            .responseTimeMs(TimeContext.getResponseTime())
            .path(TimeContext.getPath())
            .data(data)
            .build();
    }


    public static <T> ApiResponseDto<T> error(Long httpStatus, String message) {
        /*
         * error
         */
        return ApiResponseDto.<T>builder()
            .httpStatus(httpStatus)
            .statusMessage(message)
            .timeStamp(Instant.now())
            .responseTimeMs(TimeContext.getResponseTime())
            .path(TimeContext.getPath())
            .data(null)
            .build();
    }
}
