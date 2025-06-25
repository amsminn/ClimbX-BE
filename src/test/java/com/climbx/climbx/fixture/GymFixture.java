package com.climbx.climbx.fixture;

import com.climbx.climbx.gym.dto.GymInfoResponseDto;
import com.climbx.climbx.gym.entity.GymEntity;

public class GymFixture {

    public static final Double LATITUDE = 37.5665;
    public static final Double LONGITUDE = 126.9780;
    public static final String ADDRESS = "서울시 마포구 공덕";
    public static final String PHONE_NUMBER = "02-1234-5678";
    public static final String DESCRIPTION = "A premier climbing gym in Seoul.";
    public static final String MAP_2D_URL = "http://example.com/map2d";

    public static GymEntity createGymEntity(Long gymId, String name) {
        return GymEntity.builder()
            .gymId(gymId)
            .name(name)
            .latitude(LATITUDE)
            .longitude(LONGITUDE)
            .address(ADDRESS)
            .phoneNumber(PHONE_NUMBER)
            .description(DESCRIPTION)
            .map2DUrl(MAP_2D_URL)
            .build();
    }

    public static GymInfoResponseDto createGymInfoResponseDto(Long gymId, String name) {
        return GymInfoResponseDto.builder()
            .gymId(gymId)
            .name(name)
            .latitude(LATITUDE)
            .longitude(LONGITUDE)
            .address(ADDRESS)
            .phoneNumber(PHONE_NUMBER)
            .description(DESCRIPTION)
            .map2DUrl(MAP_2D_URL)
            .build();
    }
}
