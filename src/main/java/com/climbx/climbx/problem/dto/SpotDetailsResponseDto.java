package com.climbx.climbx.problem.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record SpotDetailsResponseDto(
    Long spotId,
    List<ProblemDetailsResponseDto> problemDetailsResponseDtoList
) {

    public static SpotDetailsResponseDto from(
        Long spotId,
        List<ProblemDetailsResponseDto> problemDetailsResponseDtoList
    ) {
        return SpotDetailsResponseDto.builder()
            .spotId(spotId)
            .problemDetailsResponseDtoList(problemDetailsResponseDtoList)
            .build();
    }
}
