package com.climbx.climbx.user.entity;

import com.climbx.climbx.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "user_stats")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
@Accessors(fluent = true)
@Builder
public class UserStatEntity extends BaseTimeEntity {

    @Id
    @Column(name = "user_id", updatable = false)
    @NotNull
    @Min(1)
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private  UserAccountEntity userAccountEntity;

    @Builder.Default
    @Column(name = "rating")
    @NotNull
    @Min(0)
    private Long rating = 0L; // 레이팅, 기본값은 0

    @Builder.Default
    @Column(name = "current_streak")
    @NotNull
    @Min(0)
    private Long currentStreak = 0L; // 현재 출석일, 기본값은 0

    @Builder.Default
    @Column(name = "longest_streak")
    @NotNull
    @Min(0)
    private Long longestStreak = 0L; // 가장 긴 연속 출석일, 기본값은 0

    @Builder.Default
    @Column(name = "solved_problems_count")
    @NotNull
    @Min(0)
    private Long solvedProblemsCount = 0L; // 해결한 문제 수, 기본값은 0

    @Builder.Default
    @Column(name = "rival_count")
    @NotNull
    @Min(0)
    private Long rivalCount = 0L; // 라이벌 수, 기본값은 0
}