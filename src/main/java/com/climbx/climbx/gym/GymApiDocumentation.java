package com.climbx.climbx.gym;

import com.climbx.climbx.gym.dto.GymInfoResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Tag(name = "Gym", description = "클라이밍장 관련 API")
public interface GymApiDocumentation {

    @Operation(
        summary = "클라이밍장 정보 조회",
        description = "클라이밍장 ID를 사용하여 특정 클라이밍장의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "클라이밍장 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = GymInfoResponseDto.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 클라이밍장 ID",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "클라이밍장을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        )
    })
    GymInfoResponseDto getGymById(
        @Parameter(
            name = "gymId",
            description = "클라이밍장 ID",
            required = true,
            example = "1"
        )
        @NotNull
        @Min(1L)
        Long gymId
    );

    @Operation(
        summary = "클라이밍장 목록 조회",
        description = "키워드를 사용하여 클라이밍장 목록을 조회합니다. 키워드가 없으면 모든 클라이밍장을 반환합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "클라이밍장 목록 조회 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = GymInfoResponseDto.class)))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        )
    })
    List<GymInfoResponseDto> getGymList(
        @Parameter(
            name = "keyword",
            description = "검색 키워드 (클라이밍장 이름, 주소 등)",
            required = false,
            example = "클라임"
        )
        String keyword
    );

    @Operation(
        summary = "거리 기반 클라이밍장 목록 조회",
        description = "사용자의 현재 위치(위도, 경도)를 기준으로 거리 순으로 클라이밍장 목록을 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "거리 기반 클라이밍장 목록 조회 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = GymInfoResponseDto.class)))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 위도 또는 경도",
            content = @Content(schema = @Schema(implementation = com.climbx.climbx.common.response.ApiResponse.class))
        )
    })
    List<GymInfoResponseDto> getGymListByDistance(
        @Parameter(
            name = "latitude",
            description = "위도 (-90.0 ~ 90.0)",
            required = true,
            example = "37.5665"
        )
        @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
        @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
        Double latitude,
        @Parameter(
            name = "longitude",
            description = "경도 (-180.0 ~ 180.0)",
            required = true,
            example = "126.9780"
        )
        @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
        @DecimalMax(value = "180.0", inclusive = false, message = "Longitude must be between -180 and 180")
        Double longitude,
        @Parameter(
            name = "keyword",
            description = "검색 키워드 (클라이밍장 이름, 주소 등)",
            required = false,
            example = "클라임"
        )
        String keyword
    );
} 