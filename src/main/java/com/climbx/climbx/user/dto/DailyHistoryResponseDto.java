package com.climbx.climbx.user.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

@Builder
public record DailyHistoryResponseDto(
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate date,

    @NotNull
    Long value
) {

}