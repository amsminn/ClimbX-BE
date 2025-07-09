package com.climbx.climbx.gym.dto;

import com.climbx.climbx.gym.entity.GymEntity;
import lombok.Builder;

@Builder
public record GymInfoResponseDto(

    Long gymId,
    String name,
    Double latitude,
    Double longitude,
    String address,
    String phoneNumber,
    String map2dUrl
) {

    public static GymInfoResponseDto from(GymEntity gym) {
        return GymInfoResponseDto.builder()
            .gymId(gym.gymId())
            .name(gym.name())
            .latitude(gym.latitude())
            .longitude(gym.longitude())
            .address(gym.address())
            .phoneNumber(gym.phoneNumber())
            .map2dUrl(gym.map2dUrl())
            .build();
    }
}
