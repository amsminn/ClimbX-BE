package com.climbx.climbx.fixture;

import com.climbx.climbx.gym.dto.Gym2dMapInfo;
import com.climbx.climbx.gym.dto.GymInfoResponseDto;
import com.climbx.climbx.gym.entity.GymEntity;
import java.util.List;

public class GymFixture {

    public static final String ADDRESS = "서울시 마포구 공덕";
    public static final String PHONE_NUMBER = "02-1234-5678";
    public static final Gym2dMapInfo gym2dMapInfo = new Gym2dMapInfo(
        "https://example.com/base-map.png",
        List.of("https://example.com/overlay-map1.png", "https://example.com/overlay-map2.png"));

    public static GymEntity createGymEntity(Long gymId, String name, Double latitude,
        Double longitude) {
        return GymEntity.builder()
            .gymId(gymId)
            .name(name)
            .latitude(latitude)
            .longitude(longitude)
            .address(ADDRESS)
            .phoneNumber(PHONE_NUMBER)
            .map2dUrls(gym2dMapInfo)
            .build();
    }

    public static GymInfoResponseDto createGymInfoResponseDto(Long gymId, String name,
        Double latitude, Double longitude) {
        return GymInfoResponseDto.builder()
            .gymId(gymId)
            .name(name)
            .latitude(latitude)
            .longitude(longitude)
            .address(ADDRESS)
            .phoneNumber(PHONE_NUMBER)
            .baseMapUrl(gym2dMapInfo.baseMapUrl())
            .overlayMapUrls(gym2dMapInfo.overlayMapUrls())
            .build();
    }
}
