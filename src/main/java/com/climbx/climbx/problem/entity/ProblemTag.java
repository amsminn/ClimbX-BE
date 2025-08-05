package com.climbx.climbx.problem.entity;

import com.climbx.climbx.problem.enums.ProblemTagType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "problem_tags")
@IdClass(ProblemTagId.class)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
@Accessors(fluent = true)
@Builder
public class ProblemTag {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id")
    ProblemEntity problemEntity;

    @Id
    @Column(name = "problem_id", insertable = false, updatable = false, nullable = false)
    private Long problemId;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "tag", updatable = false)
    private ProblemTagType tag;

    @Builder.Default
    @Column(name = "priority", nullable = false)
    private Integer priority = 0;

    public void addPriority(Integer priority) {
        this.priority += priority;
    }
}
