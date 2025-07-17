package com.climbx.climbx.problem.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record SpotResponseDto(

    Long gymId,
    String map2dUrl,
    List<SpotDetailsResponseDto> spotDetailsResponseDtoList
) {

    public static SpotResponseDto from(
        Long gymId,
        String map2DUrl,
        List<SpotDetailsResponseDto> spotDetailsResponseDtoList
    ) {
        return SpotResponseDto.builder()
            .gymId(gymId)
            .map2dUrl(map2DUrl)
            .spotDetailsResponseDtoList(spotDetailsResponseDtoList)
            .build();
    }
}
