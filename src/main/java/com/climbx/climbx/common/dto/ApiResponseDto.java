package com.climbx.climbx.common.dto;

import com.climbx.climbx.common.util.TimeContext;
import java.time.Instant;
import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record ApiResponseDto<T>(

    HttpStatus httpStatus,

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
            .httpStatus(HttpStatus.OK)
            .statusMessage("SUCCESS")
            .timeStamp(Instant.now())
            .responseTimeMs(TimeContext.getResponseTime())
            .path(TimeContext.getPath())
            .data(data)
            .build();
    }

    public static <T> ApiResponseDto<T> success(T data, HttpStatus httpStatus) {
        /*
         * success
         */
        return ApiResponseDto.<T>builder()
            .httpStatus(httpStatus)
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
            .httpStatus(HttpStatus.OK)
            .statusMessage(message)
            .timeStamp(Instant.now())
            .responseTimeMs(TimeContext.getResponseTime())
            .path(TimeContext.getPath())
            .data(data)
            .build();
    }


    public static <T> ApiResponseDto<T> error(HttpStatus httpStatus, String message) {
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
