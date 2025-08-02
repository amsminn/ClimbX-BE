package com.climbx.climbx.submission.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;

@Builder
public record SubmissionCreateRequestDto(

    @NotNull
    UUID videoId,

    @NotNull
    @Min(1)
    UUID problemId
) {

}