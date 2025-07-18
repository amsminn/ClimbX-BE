package com.climbx.climbx.user.dto;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record DailyHistoryResponseDto(

    LocalDate date,
    Integer value
) {

    public DailyHistoryResponseDto(java.sql.Date date, Integer value) {
        this(date.toLocalDate(), value);
    }
}