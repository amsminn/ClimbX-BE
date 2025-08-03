package com.climbx.climbx.problem.entity;

import com.climbx.climbx.common.entity.BaseTimeEntity;
import com.climbx.climbx.common.enums.ActiveStatusType;
import com.climbx.climbx.gym.entity.GymEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "problems")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
@Accessors(fluent = true)
@Builder
public class ProblemEntity extends BaseTimeEntity {

    @Id
    @Column(name = "problem_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID problemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id", nullable = false)
    private GymEntity gym;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_area_id", nullable = false)
    private GymAreaEntity gymArea;

    @Column(name = "local_level", length = 32, nullable = false)
    @Size(min = 1, max = 32)
    private String localLevel; // 클라이밍장별 레벨, 예: "빨강", "파랑", "초록" 등

    @Column(name = "hold_color", length = 32, nullable = false)
    @Size(min = 1, max = 32)
    private String holdColor; // 홀드 색상, 예: "빨강", "파랑", "초록" 등

    @Column(name = "problem_rating") // Todo nullable = false
    @Min(value = 1)
    @Max(value = 30)
    private Integer problemRating; // 문제 난이도

    @Column(name = "primary_tag", length = 32)
    @Size(max = 32) // nullable
    private String primaryTag;

    @Column(name = "secondary_tag", length = 32)
    @Size(max = 32) // nullable
    private String secondaryTag;

    @Column(name = "problem_image_cdn_url", length = 512)
    @Size(max = 512)
    private String problemImageCdnUrl; // 문제 이미지 CDN URL

    @Column(name = "active_status", length = 16, nullable = false)
    @Enumerated(EnumType.STRING)
    private ActiveStatusType activeStatus; // 문제 상태 (예: 활성화, 비활성화 등)
}