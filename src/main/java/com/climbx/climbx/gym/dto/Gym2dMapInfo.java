package com.climbx.climbx.gym.dto;

import java.util.List;

public record Gym2dMapInfo(

    String baseMapUrl,
    List<String> overlayMapUrls
) {

}
