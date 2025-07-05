package com.climbx.climbx.auth;

import com.climbx.climbx.auth.dto.LoginResponseDto;
import com.climbx.climbx.auth.dto.RefreshRequestDto;
import com.climbx.climbx.auth.dto.UserOauth2InfoResponseDto;
import com.climbx.climbx.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;

@Tag(name = "Authentication", description = "인증 관련 API")
public interface AuthApiDocumentation {

    @Operation(
        summary = "OAuth2 인증 URL 리다이렉트",
        description = "지정된 OAuth2 제공자의 인증 URL로 리다이렉트합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "302",
            description = "OAuth2 제공자로 리다이렉트 성공",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 OAuth2 제공자",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        )
    })
    ResponseEntity<ApiResponse<Void>> getOAuth2RedirectUrl(
        @Parameter(
            name = "provider",
            description = "OAuth2 제공자 (예: google, naver, kakao)",
            required = true,
            example = "google"
        )
        @NotBlank String provider
    );

    @Operation(
        summary = "OAuth2 콜백 처리",
        description = "OAuth2 인증 완료 후 콜백을 처리하고 JWT 토큰을 반환합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "인증 성공",
            content = @Content(schema = @Schema(implementation = LoginResponseDto.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 인증 코드",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        )
    })
    LoginResponseDto handleOAuth2Callback(
        @Parameter(
            name = "provider",
            description = "OAuth2 제공자",
            required = true,
            example = "google"
        )
        @NotBlank String provider,
        @Parameter(
            name = "code",
            description = "OAuth2 인증 코드",
            required = true
        )
        @NotBlank String code
    );

    @Operation(
        summary = "액세스 토큰 갱신",
        description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "토큰 갱신 성공",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "유효하지 않은 리프레시 토큰",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        )
    })
    ApiResponse<LoginResponseDto> refreshAccessToken(
        @Parameter(
            description = "리프레시 토큰 요청",
            required = true
        )
        @jakarta.validation.Valid RefreshRequestDto request
    );

    @Operation(
        summary = "현재 사용자 정보 조회",
        description = "현재 로그인한 사용자의 정보를 조회합니다."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "사용자 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = UserOauth2InfoResponseDto.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "권한이 없는 사용자",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        )
    })
    UserOauth2InfoResponseDto getCurrentUserInfo(
        @Parameter(hidden = true)
        Long userId
    );

    @Operation(
        summary = "로그아웃",
        description = "현재 로그인한 사용자를 로그아웃합니다."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "로그아웃 성공",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        )
    })
    ApiResponse<Void> signOut(
        @Parameter(
            description = "리프레시 토큰 요청",
            required = true
        )
        @jakarta.validation.Valid RefreshRequestDto request
    );
} 