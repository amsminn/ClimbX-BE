package com.climbx.climbx.common.reponse;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(
        @NonNull MethodParameter _returnType,
        @NonNull Class<? extends HttpMessageConverter<?>> _converterType
    ) {
        // ResponseEntity로 래핑할 필요가 있는지 결정
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
        @NonNull MethodParameter _returnType,
        @NonNull MediaType _selectedContentType,
        @NonNull Class<? extends HttpMessageConverter<?>> _selectedConverterType,
        @NonNull ServerHttpRequest _request,
        @NonNull ServerHttpResponse _response
    ) {
        // 1) 이미 ResponseEntity<?> 이면 그대로 돌려보내기
        if (body instanceof ResponseEntity<?>) {
            return body;
        }

        // 2) ApiResponse<?> 타입이면, 바로 ResponseEntity에 담아서 리턴
        if (body instanceof ApiResponse<?>) {
            return ResponseEntity
                .status(((ApiResponse<?>) body).httpStatus().intValue())
                .body(body);
        }

        // 3) 그 외 DTO 타입이면 ApiResponse.success로 래핑 후 ResponseEntity.ok
        ApiResponse<Object> wrapped = ApiResponse.success(body);
        return ResponseEntity.ok(wrapped);
    }
}