package com.climbx.climbx.gym;

import com.climbx.climbx.common.dto.ApiResponseDto;
import com.climbx.climbx.gym.dto.GymInfoResponseDto;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gyms")
@Validated
@RequiredArgsConstructor
public class GymController {

    private final GymService gymService;

    @GetMapping("/{gymId}")
    public ApiResponseDto<GymInfoResponseDto> getGymById(@PathVariable @NotNull @Min(1L) Long gymId) {
        GymInfoResponseDto gym = gymService.getGymById(gymId);
        return ApiResponseDto.success(gym);
    }

    @GetMapping(params = {"!latitude", "!longitude"})
    public ApiResponseDto<List<GymInfoResponseDto>> getGymList(
        @RequestParam(required = false)
        String keyword
    ) {
        List<GymInfoResponseDto> gyms = gymService.getGymList(keyword);
        return ApiResponseDto.success(gyms);
    }

    @GetMapping(params = {"latitude", "longitude"})
    public ApiResponseDto<List<GymInfoResponseDto>> getGymListByDistance(
        @RequestParam
        @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
        @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
        Double latitude,

        @RequestParam
        @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
        @DecimalMax(value = "180.0", inclusive = false, message = "Longitude must be between -180 and 180")
        Double longitude,

        @RequestParam(required = false)
        String keyword
    ) {
        List<GymInfoResponseDto> gyms = gymService.getGymListByDistance(latitude, longitude, keyword);
        return ApiResponseDto.success(gyms);
    }
}
