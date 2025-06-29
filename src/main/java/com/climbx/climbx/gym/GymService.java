package com.climbx.climbx.gym;

import com.climbx.climbx.gym.dto.GymInfoResponseDto;
import com.climbx.climbx.gym.exception.GymNotFoundException;
import com.climbx.climbx.gym.repository.GymRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GymService {

    private final GymRepository gymRepository;

    public GymInfoResponseDto getGymById(Long gymId) {
        return gymRepository.findById(gymId)
            .map(GymInfoResponseDto::from)
            .orElseThrow(() -> new GymNotFoundException(gymId));
    }

    public List<GymInfoResponseDto> getGymList(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return gymRepository.findAll().stream()
                .map(GymInfoResponseDto::from)
                .toList();
        }
        return gymRepository.findAllByNameContainingIgnoreCase(keyword).stream()
            .map(GymInfoResponseDto::from)
            .toList();
    }

    public List<GymInfoResponseDto> getGymListByDistance(
        Double latitude,
        Double longitude,
        String keyword
    ) {

        if (keyword == null || keyword.isBlank()) {
            return gymRepository.findAllByLocationOrderByDistance(latitude, longitude).stream()
                .map(GymInfoResponseDto::from)
                .toList();
        }
        return gymRepository.findAllByNameContainingIgnoreCaseOrderByDistance(
                latitude, longitude, keyword).stream()
            .map(GymInfoResponseDto::from)
            .toList();
    }
}
