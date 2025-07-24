package com.climbx.climbx.common.middleware;

import com.climbx.climbx.common.dto.ApiResponseDto;
import com.climbx.climbx.common.enums.ErrorCode;
import com.climbx.climbx.common.exception.BusinessException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
     * 비즈니스 로직에서 발생하는 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleBusinessException(BusinessException e) {
        log.error(
            "BusinessException occurred: {}, detail: {}",
            e.getMessage(),
            e.context().toString(),
            e
        );
        ApiResponseDto<Void> response = ApiResponseDto.error(
            e.errorCode().status(),
            e.errorCode().message()
        );
        return ResponseEntity
            .status(e.errorCode().status())
            .body(response);
    }

    /*
     * ConstraintViolationException 예외 처리
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleConstraintViolationException(
        ConstraintViolationException e
    ) {
        log.error("ConstraintViolationException occurred: {}", e.getMessage(), e);
        String errorMessage = e.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .reduce((msg1, msg2) -> msg1 + ", " + msg2)
            .orElse("Validation failed");
        ApiResponseDto<Void> response = ApiResponseDto.error(
            ErrorCode.VALIDATION_FAILED.status(),
            errorMessage
        );
        return ResponseEntity
            .status(ErrorCode.VALIDATION_FAILED.status())
            .body(response);
    }

    /*
     * @Valid, @Validated 를 통한 검증 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException e
    ) {
        log.error("Validation error occurred: {}", e.getMessage(), e);
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .reduce((msg1, msg2) -> msg1 + ", " + msg2)
            .orElse("Validation failed");
        ApiResponseDto<Void> response = ApiResponseDto.error(
            ErrorCode.VALIDATION_FAILED.status(),
            errorMessage
        );
        return ResponseEntity
            .status(ErrorCode.VALIDATION_FAILED.status())
            .body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleMissingServletRequestParameterException(
        MissingServletRequestParameterException e
    ) {
        log.error("MissingServletRequestParameterException occurred: {}", e.getMessage(), e);

        ApiResponseDto<Void> response = ApiResponseDto.error(
            ErrorCode.MISSING_REQUEST_PARAMETER.status(),
            e.getMessage()
        );

        return ResponseEntity
            .status(ErrorCode.MISSING_REQUEST_PARAMETER.status())
            .body(response);
    }

    /*
     * 정의되지 않은 예외
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Void>> handleGeneralException(Exception e) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
        ApiResponseDto<Void> response = ApiResponseDto.error(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred"
        );
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .body(response);
    }
}
