package com.climbx.climbx.gym;

import com.climbx.climbx.gym.dto.GymInfoResponseDto;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gyms")
@RequiredArgsConstructor
public class GymController {

    private final GymService gymService;

    @GetMapping
    public List<@Valid GymInfoResponseDto> getGymList(
        @RequestParam(required = false) Double latitude,
        @RequestParam(required = false) Double longitude,
        @RequestParam(required = false) String keyword
    ) {

        if (latitude != null && longitude != null) {
            return gymService.getGymListByDistance(latitude, longitude, keyword);
        }

        return gymService.getGymList(keyword);
    }

}
