package com.climbx.climbx.user.entity;

import com.climbx.climbx.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "user_accounts")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
@Setter
@Accessors(fluent = true, chain = true)
@Builder
public class UserAccount extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false, nullable = false)
    private Long userId; // 사용자 ID

    @Column(name = "role", length = 20, nullable = false)
    private String role; // USER, ADMIN 등 권한

    @Column(name = "nickname", length = 50, nullable = false, unique = true)
    private String nickname; // 사용자 닉네임

    @Column(name = "status_message", length = 100, nullable = true)
    private String statusMessage; // 상태 메시지

    @Column(name = "profile_image_url", length = 255, nullable = true)
    private String profileImageUrl; // 프로필 이미지 URL

    @Builder.Default
    @Column(name = "last_login_date", nullable = false)
    private LocalDate lastLoginDate = LocalDate.now(); // 마지막 접속 날짜, 기본값은 현재 날짜

    @OneToOne(mappedBy = "userAccount", fetch = FetchType.LAZY, optional = false)
    private UserStat userStat;

    public void markLogin() {
        this.lastLoginDate = LocalDate.now(); // 현재 날짜로 마지막 접속 날짜 갱신
    }
}