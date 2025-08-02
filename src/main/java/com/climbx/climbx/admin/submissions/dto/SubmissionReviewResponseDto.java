package com.climbx.climbx.admin.submissions.dto;

import com.climbx.climbx.common.enums.StatusType;
import java.util.UUID;
import lombok.Builder;

@Builder
public record SubmissionReviewResponseDto(

    UUID videoId,
    StatusType status,
    String reason
) {

}
