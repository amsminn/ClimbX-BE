package com.climbx.climbx.common.dto;

import com.climbx.climbx.common.util.TimeContext;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record ApiResponseDto<T>(

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    HttpStatus httpStatus,

    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",           // ISO-8601 UTC 포맷 (밀리초까지, 끝에 Z로 UTC 표시)
        timezone = "UTC"                                     // 입력·출력 시 UTC 타임존 기준으로 변환
    )
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
