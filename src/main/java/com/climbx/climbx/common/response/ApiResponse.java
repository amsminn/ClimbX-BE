package com.climbx.climbx.common.response;

import com.climbx.climbx.common.error.ErrorCode;
import com.climbx.climbx.common.timeTracking.TimeContext;
import java.time.Instant;
import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record ApiResponse<T>(

    Long httpStatus,
    String statusMessage,
    Instant timeStamp,
    Long responseTimeMs,
    String path,
    T data
) {

    public static <T> ApiResponse<T> success(T data) {
        /*
         * success
         */
        return ApiResponse.<T>builder()
            .httpStatus((long) HttpStatus.OK.value())
            .statusMessage("SUCCESS")
            .timeStamp(Instant.now())
            .responseTimeMs(TimeContext.getResponseTime())
            .path(TimeContext.getPath())
            .data(data)
            .build();
    }

    public static <T> ApiResponse<T> success(T data, HttpStatus httpStatus) {
        /*
         * success
         */
        return ApiResponse.<T>builder()
            .httpStatus((long) httpStatus.value())
            .statusMessage("SUCCESS")
            .timeStamp(Instant.now())
            .responseTimeMs(TimeContext.getResponseTime())
            .path(TimeContext.getPath())
            .data(data)
            .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        /*
         * success with custom message
         */
        return ApiResponse.<T>builder()
            .httpStatus((long) HttpStatus.OK.value())
            .statusMessage(message)
            .timeStamp(Instant.now())
            .responseTimeMs(TimeContext.getResponseTime())
            .path(TimeContext.getPath())
            .data(data)
            .build();
    }

<<<<<<< HEAD
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
=======
    public static <T> ApiResponse<T> error(com.climbx.climbx.common.error.ErrorCode errorCode) {
>>>>>>> 8947ec5 (refactor: 인증 관련 DTO, 예외 처리, JWT 필터 및 테스트 코드 리팩토링)
        return ApiResponse.<T>builder()
            .httpStatus((long) errorCode.status().value())
            .statusMessage(errorCode.message())
            .timeStamp(Instant.now())
            .responseTimeMs(TimeContext.getResponseTime())
            .path(TimeContext.getPath())
            .data(null)
            .build();
    }

    public static <T> ApiResponse<T> error(HttpStatus httpStatus, String message) {
        /*
         * error
         */
        return ApiResponse.<T>builder()
            .httpStatus((long) httpStatus.value())
            .statusMessage(message)
            .timeStamp(Instant.now())
            .responseTimeMs(TimeContext.getResponseTime())
            .path(TimeContext.getPath())
            .data(null)
            .build();
    }

}
