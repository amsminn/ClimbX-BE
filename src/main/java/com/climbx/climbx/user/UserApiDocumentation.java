package com.climbx.climbx.user;

import com.climbx.climbx.common.enums.UserHistoryCriteriaType;
import com.climbx.climbx.problem.dto.ProblemResponseDto;
import com.climbx.climbx.user.dto.DailyHistoryResponseDto;
import com.climbx.climbx.user.dto.UserProfileModifyRequestDto;
import com.climbx.climbx.user.dto.UserProfileResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "User", description = "사용자 관련 API")
public interface UserApiDocumentation {

    @Operation(
        summary = "사용자 목록 조회",
        description = "검색 키워드를 사용하여 사용자 목록을 조회합니다. 키워드가 없으면 모든 사용자를 반환합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "사용자 목록 조회 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserProfileResponseDto.class)))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        )
    })
    List<UserProfileResponseDto> getUsers(
        @Parameter(
            name = "search",
            description = "검색 키워드 (닉네임, 이름 등)",
            required = false,
            example = "클라이머"
        )
        String search
    );

    @Operation(
        summary = "사용자 프로필 조회",
        description = "닉네임을 사용하여 특정 사용자의 프로필 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "사용자 프로필 조회 성공",
            content = @Content(schema = @Schema(implementation = UserProfileResponseDto.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 닉네임",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        )
    })
    UserProfileResponseDto getUserByNickname(
        @Parameter(
            name = "nickname",
            description = "사용자 닉네임",
            required = true,
            example = "클라이머123"
        )
        @NotBlank
        String nickname
    );

    @Operation(
        summary = "사용자 프로필 수정",
        description = "현재 로그인한 사용자의 프로필 정보를 수정합니다."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "프로필 수정 성공",
            content = @Content(schema = @Schema(implementation = UserProfileResponseDto.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 또는 유효하지 않은 데이터",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "권한 없음 (다른 사용자의 프로필)",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "중복된 닉네임",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        )
    })
    UserProfileResponseDto modifyUserProfile(
        @Parameter(hidden = true)
        Long userId,
        @Parameter(
            name = "nickname",
            description = "현재 사용자 닉네임",
            required = true,
            example = "클라이머123"
        )
        @NotBlank
        String nickname,
        @Parameter(
            description = "프로필 수정 요청 데이터",
            required = true
        )
        @jakarta.validation.Valid UserProfileModifyRequestDto request
    );

    @Operation(
        summary = "사용자 최고 난이도 문제 조회",
        description = "특정 사용자가 완등한 최고 난이도 문제들을 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "최고 난이도 문제 조회 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProblemResponseDto.class)))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        )
    })
    List<ProblemResponseDto> getUserTopProblems(
        @Parameter(
            name = "nickname",
            description = "사용자 닉네임",
            required = true,
            example = "클라이머123"
        )
        @NotBlank
        String nickname,
        @Parameter(
            name = "limit",
            description = "조회할 문제 수 (1-20)",
            required = false,
            example = "20"
        )
        @Min(1)
        @Max(20)
        Integer limit
    );

    @Operation(
        summary = "사용자 연속 등반 기록 조회",
        description = "특정 사용자의 연속 등반 기록을 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "연속 등반 기록 조회 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = DailyHistoryResponseDto.class)))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        )
    })
    List<DailyHistoryResponseDto> getUserStreak(
        @Parameter(
            name = "nickname",
            description = "사용자 닉네임",
            required = true,
            example = "클라이머123"
        )
        @NotBlank
        String nickname,
        @Parameter(
            name = "from",
            description = "조회 시작 날짜 (YYYY-MM-DD)",
            required = false,
            example = "2023-01-01"
        )
        LocalDate from,
        @Parameter(
            name = "to",
            description = "조회 종료 날짜 (YYYY-MM-DD)",
            required = false,
            example = "2023-12-31"
        )
        LocalDate to
    );

    @Operation(
        summary = "사용자 일별 기록 조회",
        description = "특정 사용자의 일별 등반 기록을 조건에 따라 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "일별 기록 조회 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = DailyHistoryResponseDto.class)))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        )
    })
    List<DailyHistoryResponseDto> getUserDailyHistory(
        @Parameter(
            name = "nickname",
            description = "사용자 닉네임",
            required = true,
            example = "클라이머123"
        )
        @NotBlank
        String nickname,
        @Parameter(
            name = "criteria",
            description = "조회 기준",
            required = true,
            schema = @Schema(implementation = UserHistoryCriteriaType.class)
        )
        @NotNull
        UserHistoryCriteriaType criteria,
        @Parameter(
            name = "from",
            description = "조회 시작 날짜 (YYYY-MM-DD)",
            required = false,
            example = "2023-01-01"
        )
        LocalDate from,
        @Parameter(
            name = "to",
            description = "조회 종료 날짜 (YYYY-MM-DD)",
            required = false,
            example = "2023-12-31"
        )
        LocalDate to
    );
} 