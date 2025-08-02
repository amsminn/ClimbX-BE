package com.climbx.climbx.problem;

import com.climbx.climbx.common.dto.ApiResponseDto;
import com.climbx.climbx.problem.dto.ProblemCreateRequestDto;
import com.climbx.climbx.problem.dto.ProblemCreateResponseDto;
import com.climbx.climbx.problem.dto.SpotResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

/**
 * 문제 관련 API 문서화를 위한 인터페이스
 * <p>
 * 이 인터페이스는 클라이밍 문제와 관련된 API 엔드포인트들을 정의합니다. Swagger UI에서 "Problem" 태그로 그룹화됩니다.
 */
@Validated
@Tag(name = "Problem", description = "클라이밍 문제 관련 API")
public interface ProblemApiDocumentation {

    @Operation(
        summary = "문제 목록 조회",
        description = "클라이밍장 ID, 레벨, 홀드 색상 조건으로 문제 목록을 조회합니다. 모든 파라미터가 필수이며, 조건에 맞는 문제들을 spotId로 그룹화하여 반환합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "문제 목록 조회 성공",
            content = @Content(
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    name = "문제 목록",
                    value = """
                        {
                          "httpStatus": 200,
                          "statusMessage": "SUCCESS",
                          "timeStamp": "2024-01-01T10:00:00Z",
                          "responseTimeMs": 145,
                          "path": "/api/problems",
                          "data": {
                            "gymId": 1,
                            "map2DUrl": "http://fake-url",
                            "spotDetailsResponseDtoList": [
                              {
                                "spotId": 1,
                                "problemDetailsResponseDtoList": [
                                  {
                                    "problemId": 1,
                                    "gymId": 1,
                                    "gymName": "테스트 클라이밍장",
                                    "localLevel": "빨강",
                                    "holdColor": "파랑",
                                    "problemRating": 1200,
                                    "spotId": 1,
                                    "spotXRatio": 15.5,
                                    "spotYRatio": 20.3,
                                    "imageUrl": "https://example.com/problem1.jpg"
                                  },
                                  {
                                    "problemId": 2,
                                    "gymId": 1,
                                    "gymName": "테스트 클라이밍장",
                                    "localLevel": "빨강",
                                    "holdColor": "파랑",
                                    "problemRating": 1300,
                                    "spotId": 1,
                                    "spotXRatio": 16.2,
                                    "spotYRatio": 21.1,
                                    "imageUrl": "https://example.com/problem2.jpg"
                                  }
                                ]
                              },
                              {
                                "spotId": 2,
                                "problemDetailsResponseDtoList": [
                                  {
                                    "problemId": 3,
                                    "gymId": 1,
                                    "gymName": "테스트 클라이밍장",
                                    "localLevel": "빨강",
                                    "holdColor": "파랑",
                                    "problemRating": 1400,
                                    "spotId": 2,
                                    "spotXRatio": 45.8,
                                    "spotYRatio": 60.5,
                                    "imageUrl": "https://example.com/problem3.jpg"
                                  }
                                ]
                              }
                            ]
                          }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 또는 필수 파라미터 누락",
            content = @Content(
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    name = "잘못된 요청",
                    value = """
                        {
                          "httpStatus": 400,
                          "statusMessage": "모든 파라미터(gymId, localLevel, holdColor)가 필요합니다.",
                          "timeStamp": "2024-01-01T10:00:00Z",
                          "responseTimeMs": 45,
                          "path": "/api/problems",
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
                examples = @ExampleObject(
                    name = "서버 오류",
                    value = """
                        {
                          "httpStatus": 500,
                          "statusMessage": "서버 내부 오류가 발생했습니다.",
                          "timeStamp": "2024-01-01T10:00:00Z",
                          "responseTimeMs": 123,
                          "path": "/api/problems",
                          "data": null
                        }
                        """
                )
            )
        )
    })
    SpotResponseDto getProblemSpotsWithFilters(
        @Parameter(
            name = "gymId",
            description = "클라이밍장 ID",
            required = true,
            example = "1"
        )
        @Min(1L)
        Long gymId,

        @Parameter(
            name = "localLevel",
            description = "문제 레벨 (클라이밍장별 난이도)",
            required = true,
            example = "빨강"
        )
        @Size(min = 1, max = 20)
        String localLevel,

        @Parameter(
            name = "holdColor",
            description = "홀드 색상",
            required = true,
            example = "초록"
        )
        @Size(min = 1, max = 20)
        String holdColor
    );

    @Operation(
        summary = "문제 생성",
        description = "새로운 클라이밍 문제를 생성합니다. 문제 정보와 선택적으로 이미지를 업로드할 수 있습니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "문제 생성 성공",
            content = @Content(
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    name = "문제 생성 성공",
                    value = """
                        {
                          "httpStatus": 201,
                          "statusMessage": "SUCCESS",
                          "timeStamp": "2024-01-01T10:00:00Z",
                          "responseTimeMs": 234,
                          "path": "/api/problems",
                          "data": {
                            "problemId": 123,
                            "gymAreaId": 1,
                            "localLevel": "V3",
                            "holdColor": "빨강",
                            "problemRating": 1500,
                            "spotId": 5,
                            "spotXRatio": 25.5,
                            "spotYRatio": 30.2,
                            "imageCdnUrl": "https://cdn.example.com/problem-images/1_1640995200000.jpg",
                            "status": "ACTIVE"
                          }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 또는 유효성 검사 실패",
            content = @Content(
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    name = "유효성 검사 실패",
                    value = """
                        {
                          "httpStatus": 400,
                          "statusMessage": "VALIDATION_FAILED",
                          "timeStamp": "2024-01-01T10:00:00Z",
                          "responseTimeMs": 45,
                          "path": "/api/problems",
                          "data": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "클라이밍장 영역을 찾을 수 없음",
            content = @Content(
                schema = @Schema(implementation = ApiResponseDto.class),
                examples = @ExampleObject(
                    name = "영역 없음",
                    value = """
                        {
                          "httpStatus": 404,
                          "statusMessage": "GYM_AREA_NOT_FOUND",
                          "timeStamp": "2024-01-01T10:00:00Z",
                          "responseTimeMs": 123,
                          "path": "/api/problems",
                          "data": null
                        }
                        """
                )
            )
        )
    })
    ProblemCreateResponseDto registerProblem(
        @Parameter(
            description = "문제 생성 요청 데이터",
            required = true
        )
        @Valid ProblemCreateRequestDto request,

        @Parameter(
            description = "문제 이미지 파일 (선택사항, 최대 10MB)",
            required = false
        )
        MultipartFile problemImage
    );
}