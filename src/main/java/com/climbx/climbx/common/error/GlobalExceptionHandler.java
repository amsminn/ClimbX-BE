package com.climbx.climbx.common.error;

import com.climbx.climbx.common.reponse.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
     * 비즈니스 로직에서 발생하는 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.error(
            "BusinessException occurred: {}, detail: {}",
            e.getMessage(),
            e.context().toString(),
            e
        );
        ApiResponse<Void> response = ApiResponse.error(e.errorCode().status(),
            e.errorCode().message());
        return ResponseEntity.status(e.errorCode().status()).body(response);
    }

    /*
     * @Valid, @Validated 를 통한 검증 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException e) {
        log.error("Validation error occurred: {}", e.getMessage(), e);
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .reduce((msg1, msg2) -> msg1 + ", " + msg2)
            .orElse("Validation failed");
        ApiResponse<Void> response = ApiResponse.error(
            ErrorCode.INVALID_REQUEST.status(),
            errorMessage
        );
        return ResponseEntity.badRequest().body(response);
    }

    /*
     * 정의되지 않은 예외
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception e) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
        ApiResponse<Void> response = ApiResponse.error(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred"
        );
        return ResponseEntity.status(500).body(response);
    }
}
