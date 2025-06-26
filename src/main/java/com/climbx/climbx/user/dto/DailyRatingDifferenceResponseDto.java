package com.climbx.climbx.user.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record DailyRatingDifferenceResponseDto(

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate date,

    @NotNull
    @Min(0)
    Long currentRating,

    @NotNull
    @Min(0)
    Long previousRating,

    @NotNull
    Long ratingDifference
) {

}
