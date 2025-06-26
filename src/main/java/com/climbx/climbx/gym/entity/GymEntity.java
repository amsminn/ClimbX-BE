package com.climbx.climbx.gym.entity;

import com.climbx.climbx.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "gyms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Accessors(fluent = true)
@Builder
public class GymEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gym_id", updatable = false)
    @NotNull
    private Long gymId; // 클라이밍장 ID

    @Column(name = "name", length = 30)
    @NotBlank
    @Size(min = 1, max = 30)
    private String name; // 클라이밍장 이름

    @Column(name = "latitude")
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double latitude; // 위도

    @Column(name = "longitude")
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0", inclusive = false)
    private Double longitude; // 경도

    @Column(name = "address", length = 100)
    private String address; // 주소

    @Column(name = "phone_number", length = 30)
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$")
    private String phoneNumber; // 전화번호, 형식: 010-1234-5678

    @Column(name = "description", length = 200)
    private String description; // 상세 설명

    @Column(name = "map_2d_url", length = 255)
    private String map2DUrl; // 2D 지도 URL

    // Todo : 매장 메타데이터 json 컬럼
}
