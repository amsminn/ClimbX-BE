package com.climbx.climbx.gym;

import com.climbx.climbx.gym.dto.GymInfoResponseDto;
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
public class GymController implements GymApiDocumentation {

    private final GymService gymService;

    @GetMapping("/{gymId}")
    public GymInfoResponseDto getGymById(
        @PathVariable
        Long gymId
    ) {
        return gymService.getGymById(gymId);
    }

    @GetMapping(params = {"!latitude", "!longitude"})
    public List<GymInfoResponseDto> getGymList(
        @RequestParam(required = false)
        String keyword
    ) {
        return gymService.getGymList(keyword);
    }

    @GetMapping(params = {"latitude", "longitude"})
    public List<GymInfoResponseDto> getGymListByDistance(
        @RequestParam
        Double latitude,

        @RequestParam
        Double longitude,

        @RequestParam(required = false)
        String keyword
    ) {
        return gymService.getGymListByDistance(latitude, longitude,
            keyword);
    }
}
