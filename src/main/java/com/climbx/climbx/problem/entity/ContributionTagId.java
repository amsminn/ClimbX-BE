package com.climbx.climbx.problem.entity;

import jakarta.persistence.Column;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
@Accessors(fluent = true)
@Builder
public class ContributionTagId implements Serializable {

    @Column(name = "contribution_id")
    private Long contributionId;

    @Column(name = "tag", length = 16, nullable = false)
    private String tag;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContributionTagId)) {
            return false;
        }

        ContributionTagId that = (ContributionTagId) o;

        if (!contributionId.equals(that.contributionId)) {
            return false;
        }
        return tag.equals(that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contributionId, tag);
    }
}
