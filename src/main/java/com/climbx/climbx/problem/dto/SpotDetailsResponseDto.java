package com.climbx.climbx.problem.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record SpotDetailsResponseDto(

    Long spotId,
    List<ProblemInfoInSpotResponseDto> problemDetailsResponseDtoList
) {

    public static SpotDetailsResponseDto from(
        Long spotId,
        List<ProblemInfoInSpotResponseDto> problemInfoInSpotResponseDtoList
    ) {
        return SpotDetailsResponseDto.builder()
            .spotId(spotId)
            .problemDetailsResponseDtoList(problemInfoInSpotResponseDtoList)
            .build();
    }
}
