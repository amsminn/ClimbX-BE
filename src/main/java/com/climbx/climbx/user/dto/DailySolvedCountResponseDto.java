package com.climbx.climbx.user.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

@Builder
public record DailySolvedCountResponseDto(

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate date,

    @NotNull
    @Min(1)
    Long solvedCount
) {

    public static DailySolvedCountResponseDto from(Object[] obj) {
        // java.sql.Date를 LocalDate로 변환
        LocalDate date = obj[0] instanceof java.sql.Date 
            ? ((java.sql.Date) obj[0]).toLocalDate()
            : (LocalDate) obj[0];
            
        return DailySolvedCountResponseDto.builder()
            .date(date)
            .solvedCount((Long) obj[1])
            .build();
    }
}
