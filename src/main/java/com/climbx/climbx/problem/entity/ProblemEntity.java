package com.climbx.climbx.problem.entity;

import com.climbx.climbx.common.entity.BaseTimeEntity;
import com.climbx.climbx.gym.entity.GymEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "problem_id", updatable = false, nullable = false)
    private Long problemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id", nullable = false)
    private GymEntity gym;

    @Column(name = "local_level", length = 20, nullable = false)
    @Size(min = 1, max = 20)
    private String localLevel; // 클라이밍장별 레벨, 예: "빨강", "파랑", "초록" 등

    @Column(name = "hold_color", length = 20, nullable = false)
    @Size(min = 1, max = 20)
    private String holdColor; // 홀드 색상, 예: "빨강", "파랑", "초록" 등

    @Column(name = "problem_rating", nullable = false)
    @Min(value = 1L)
    private Long problemRating; // 문제 난이도

    @Column(name = "spot_id", nullable = false)
    @Min(value = 1L)
    private Long spotId; // 문제 위치 ID, 클라이밍장 내에서의 위치

    @Column(name = "spot_x_ratio", nullable = false)
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private Double spotXRatio; // 문제 위치 X 좌표 비율

    @Column(name = "spot_y_ratio", nullable = false)
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private Double spotYRatio; // 문제 위치 Y 좌표 비율

    @Column(name = "image_url", length = 255)
    @Size(max = 255)
    private String imageUrl; // 문제 이미지 URL

}