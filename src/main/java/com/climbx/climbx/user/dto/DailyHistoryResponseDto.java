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

    public static DailyHistoryResponseDto from(Object[] obj) {
        // java.sql.Date를 LocalDate로 변환
        LocalDate date = obj[0] instanceof java.sql.Date 
            ? ((java.sql.Date) obj[0]).toLocalDate()
            : (LocalDate) obj[0];
            
        Long value = obj[1] instanceof Number 
            ? ((Number) obj[1]).longValue()
            : (Long) obj[1];
            
        return new DailyHistoryResponseDto(date, value);
    }
}