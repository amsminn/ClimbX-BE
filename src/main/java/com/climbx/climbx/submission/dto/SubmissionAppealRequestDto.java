package com.climbx.climbx.submission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubmissionAppealRequestDto(

    @NotBlank
    @Size(max = 256)
    String reason
) {

}
