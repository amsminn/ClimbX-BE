package com.climbx.climbx.ranking;

import com.climbx.climbx.ranking.dto.RankingResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;

@Validated
@Tag(name = "Ranking", description = "랭킹 관련 API")
public interface RankingApiDocumentation {

    @Operation(
        operationId = "getRanking",
        summary = "사용자 랭킹 조회",
        description = """
            다양한 기준으로 사용자 랭킹을 조회합니다.
            
            **지원하는 랭킹 기준**:
            - `rating`: 레이팅 기준
            - `streak`: 연속 출석일 기준
            - `longest_streak`: 최장 연속 출석일 기준
            - `solved_count`: 해결한 문제 수 기준
            
            **정렬 순서**:
            - `desc`: 내림차순 (기본값)
            - `asc`: 오름차순
            
            **주의사항**:
            - 관리자 계정은 랭킹에서 제외됩니다
            - 페이지 번호는 0부터 시작합니다
            """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "랭킹 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class),
                examples = @ExampleObject(
                    name = "레이팅 기준 랭킹 조회 성공",
                    value = """
                        {
                          "httpStatus": 200,
                          "statusMessage": "SUCCESS",
                          "timeStamp": "2024-01-01T10:00:00Z",
                          "responseTimeMs": 145,
                          "path": "/api/ranking/users?criteria=rating&order=desc&page=0&perPage=10",
                          "data": {
                            "totalCount": 150,
                            "page": 0,
                            "perPage": 10,
                            "totalPage": 15,
                            "rankingList": [
                              {
                                "nickname": "alice",
                                "statusMessage": "열심히 클라이밍 중!",
                                "profileImageUrl": "https://example.com/profile/alice.jpg",
                                "rating": 1800,
                                "currentStreak": 25,
                                "longestStreak": 45,
                                "solvedCount": 120
                              },
                              {
                                "nickname": "bob",
                                "statusMessage": "클라이밍 마스터가 되겠다!",
                                "profileImageUrl": "https://example.com/profile/bob.jpg",
                                "rating": 1650,
                                "currentStreak": 12,
                                "longestStreak": 30,
                                "solvedCount": 85
                              }
                            ]
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class),
                examples = @ExampleObject(
                    name = "잘못된 랭킹 기준",
                    value = """
                        {
                          "httpStatus": 400,
                          "statusMessage": "잘못된 랭킹 기준입니다.",
                          "timeStamp": "2024-01-01T10:00:00Z",
                          "responseTimeMs": 87,
                          "path": "/api/ranking/users?criteria=invalid_criteria",
                          "data": null,
                          "errors": {
                            "criteria": "invalid_criteria"
                          }
                        }
                        """
                )
            )
        )
    })
    RankingResponseDto getRanking(
        @Parameter(
            name = "criteria",
            description = "랭킹 기준 (rating, streak, longestStreak, solvedProblemsCount)",
            required = true,
            example = "rating"
        )
        @NotBlank(message = "랭킹 기준은 필수입니다")
        String criteria,

        @Parameter(
            name = "order",
            description = "정렬 순서 (asc: 오름차순, desc: 내림차순)",
            required = false,
            example = "desc"
        )
        @Pattern(regexp = "^(asc|desc)$", message = "정렬 순서는 asc 또는 desc만 가능합니다")
        String order,

        @Parameter(
            name = "page",
            description = "페이지 번호 (0부터 시작)",
            required = false,
            example = "0"
        )
        @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다")
        Integer page,

        @Parameter(
            name = "perPage",
            description = "페이지당 항목 수 (최대 100)",
            required = false,
            example = "20"
        )
        @Min(value = 1, message = "페이지당 항목 수는 1 이상이어야 합니다")
        Integer perPage
    );
} 