package com.climbx.climbx.problem.entity;

import com.climbx.climbx.problem.enums.ProblemTagType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class ProblemTagId implements Serializable {

    @Column(name = "problem_id")
    private Long problemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    private ProblemTagType tag;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProblemTagId)) {
            return false;
        }

        ProblemTagId that = (ProblemTagId) o;

        if (!problemId.equals(that.problemId)) {
            return false;
        }
        return tag == that.tag;
    }

    @Override
    public int hashCode() {
        return Objects.hash(problemId, tag);
    }
}
