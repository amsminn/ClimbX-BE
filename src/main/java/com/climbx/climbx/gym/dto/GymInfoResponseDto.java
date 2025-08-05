package com.climbx.climbx.gym.dto;

import com.climbx.climbx.gym.entity.GymEntity;
import java.util.List;
import lombok.Builder;

@Builder
public record GymInfoResponseDto(

    Long gymId,
    String name,
    Double latitude,
    Double longitude,
    String address,
    String phoneNumber,
    String baseMapUrl,
    List<String> overlayMapUrls
) {

    public static GymInfoResponseDto from(GymEntity gym) {
        return GymInfoResponseDto.builder()
            .gymId(gym.gymId())
            .name(gym.name())
            .latitude(gym.latitude())
            .longitude(gym.longitude())
            .address(gym.address())
            .phoneNumber(gym.phoneNumber())
            .baseMapUrl(gym.map2dUrls() == null ? null : gym.map2dUrls().baseMapUrl())
            .overlayMapUrls(gym.map2dUrls() == null
                ? List.of()
                : gym.map2dUrls().overlayMapUrls() == null
                    ? List.of()
                    : gym.map2dUrls().overlayMapUrls())
            .build();
    }
}
