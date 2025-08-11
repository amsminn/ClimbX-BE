package com.climbx.climbx.admin.gym;

import com.climbx.climbx.common.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Validated
@Tag(name = "Admin Gym", description = "관리자 클라이밍장 관리 API")
public interface AdminGymApiDocumentation {

    @Operation(
        summary = "클라이밍장 2D 맵 이미지 업로드",
        description = "관리자가 클라이밍장의 2D 맵 이미지를 업로드합니다. 기본 이미지 1개와 오버레이 이미지 여러 개를 S3에 업로드하고, 데이터베이스에 CDN URL을 JSON 형태로 저장합니다."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "2D 맵 이미지 업로드 완료",
            content = @Content(
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    name = "업로드 성공",
                    value = """
                        {
                          "httpStatus": 200,
                          "statusMessage": "SUCCESS",
                          "timeStamp": "2024-01-01T10:00:00Z",
                          "responseTimeMs": 2340,
                          "path": "/api/admin/gyms/upload/2d-map",
                          "data": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터",
            content = @Content(
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = {
                    @ExampleObject(
                        name = "필수 파라미터 누락",
                        value = """
                            {
                              "httpStatus": 400,
                              "statusMessage": "필수 파라미터가 누락되었습니다.",
                              "timeStamp": "2024-01-01T10:00:00Z",
                              "responseTimeMs": 45,
                              "path": "/api/admin/gyms/upload/2d-map",
                              "data": null
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "지원하지 않는 파일 형식",
                        value = """
                            {
                              "httpStatus": 400,
                              "statusMessage": "지원하지 않는 파일 형식입니다.",
                              "timeStamp": "2024-01-01T10:00:00Z",
                              "responseTimeMs": 67,
                              "path": "/api/admin/gyms/upload/2d-map",
                              "data": null
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    name = "인증되지 않은 사용자",
                    value = """
                        {
                          "httpStatus": 401,
                          "statusMessage": "인증이 필요합니다.",
                          "timeStamp": "2024-01-01T10:00:00Z",
                          "responseTimeMs": 23,
                          "path": "/api/admin/gyms/upload/2d-map",
                          "data": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "권한 없음 (관리자 권한 필요)",
            content = @Content(
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    name = "관리자 권한 없음",
                    value = """
                        {
                          "httpStatus": 403,
                          "statusMessage": "관리자 권한이 필요합니다.",
                          "timeStamp": "2024-01-01T10:00:00Z",
                          "responseTimeMs": 34,
                          "path": "/api/admin/gyms/upload/2d-map",
                          "data": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "클라이밍장을 찾을 수 없음",
            content = @Content(
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    name = "클라이밍장 없음",
                    value = """
                        {
                          "httpStatus": 404,
                          "statusMessage": "해당 클라이밍장을 찾을 수 없습니다.",
                          "timeStamp": "2024-01-01T10:00:00Z",
                          "responseTimeMs": 89,
                          "path": "/api/admin/gyms/upload/2d-map",
                          "data": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "413",
            description = "파일 크기 초과",
            content = @Content(
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    name = "파일 크기 초과",
                    value = """
                        {
                          "httpStatus": 413,
                          "statusMessage": "업로드 파일 크기가 허용된 한도를 초과했습니다.",
                          "timeStamp": "2024-01-01T10:00:00Z",
                          "responseTimeMs": 156,
                          "path": "/api/admin/gyms/upload/2d-map",
                          "data": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = {
                    @ExampleObject(
                        name = "S3 업로드 실패",
                        value = """
                            {
                              "httpStatus": 500,
                              "statusMessage": "S3 업로드 중 오류가 발생했습니다.",
                              "timeStamp": "2024-01-01T10:00:00Z",
                              "responseTimeMs": 1234,
                              "path": "/api/admin/gyms/upload/2d-map",
                              "data": null
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "서버 오류",
                        value = """
                            {
                              "httpStatus": 500,
                              "statusMessage": "서버 내부 오류가 발생했습니다.",
                              "timeStamp": "2024-01-01T10:00:00Z",
                              "responseTimeMs": 123,
                              "path": "/api/admin/gyms/upload/2d-map",
                              "data": null
                            }
                            """
                    )
                }
            )
        )
    })
    void uploadGym2dMap(
        @Parameter(
            name = "gymId",
            description = "2D 맵을 업로드할 클라이밍장의 ID",
            required = true,
            example = "1"
        )
        Long gymId,

        @Parameter(
            name = "",
            description = "클라이밍장의 오버레이 2D 맵 이미지 파일들 (벽이름.png 형태로 업로드, PNG, JPG, JPEG 지원)",
            required = true
        )
        MultipartHttpServletRequest request
    );
}