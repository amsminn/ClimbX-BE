package com.climbx.climbx.fixture;

import com.climbx.climbx.gym.entity.GymEntity;
import com.climbx.climbx.problem.entity.GymAreaEntity;

public class GymAreaFixture {

    public static final String DEFAULT_GYM_AREA_IMAGE_CDN_URL = "http://example.com/area-image.jpg";

    public static GymAreaEntity createGymAreaEntity(Long gymAreaId, GymEntity gym,
        String areaName) {
        return GymAreaEntity.builder()
            .gymAreaId(gymAreaId)
            .gym(gym)
            .areaName(areaName)
            .areaImageCdnUrl(DEFAULT_GYM_AREA_IMAGE_CDN_URL)
            .build();
    }
}
