package com.climbx.climbx.gym.dto;

import com.climbx.climbx.gym.entity.GymEntity;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record GymInfoResponseDto(

    @NotNull
    Long gymId,

    @NotBlank
    @Size(min = 1, max = 30)
    String name,

    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    Double latitude,

    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    Double longitude,

    @Size(max = 100)
    String address,

    @Size(min = 11, max = 13)
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$")
    String phoneNumber,

    @Size(max = 200)
    String description,

    @Size(max = 255)
    String map2DUrl
) {

    public static GymInfoResponseDto from(GymEntity gym) {
        return GymInfoResponseDto.builder()
            .gymId(gym.gymId())
            .name(gym.name())
            .latitude(gym.latitude())
            .longitude(gym.longitude())
            .address(gym.address())
            .phoneNumber(gym.phoneNumber())
            .description(gym.description())
            .map2DUrl(gym.map2DUrl())
            .build();
    }
}
