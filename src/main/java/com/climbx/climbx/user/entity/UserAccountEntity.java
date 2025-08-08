package com.climbx.climbx.user.entity;

import com.climbx.climbx.auth.entity.UserAuthEntity;
import com.climbx.climbx.common.entity.BaseTimeEntity;
import com.climbx.climbx.video.entity.VideoEntity;
import com.climbx.climbx.submission.entity.SubmissionEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "user_accounts")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
@Accessors(fluent = true)
@Builder
public class UserAccountEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false, nullable = false)
    private Long userId; // 사용자 ID

    @Column(name = "role", length = 20, nullable = false)
    @NotNull
    @Size(max = 20)
    private String role; // USER, ADMIN 등 권한

    @Column(name = "nickname", length = 50, unique = true, nullable = false)
    @NotBlank
    @Size(min = 2, max = 50)
    private String nickname; // 사용자 닉네임

    @Column(name = "email", length = 100, nullable = true)
    @Email
    @Size(max = 100)
    private String email; // 사용자 이메일 (주 이메일)

    @Column(name = "status_message", length = 100, nullable = true)
    @Size(max = 100) // nullable
    private String statusMessage; // 상태 메시지

    @Column(name = "profile_image_url", length = 255, nullable = true)
    private String profileImageUrl; // 프로필 이미지 URL

    @Builder.Default
    @Column(name = "last_login_date", nullable = false)
    private LocalDate lastLoginDate = LocalDate.now(); // 마지막 접속 날짜, 기본값은 현재 날짜

    @OneToOne(mappedBy = "userAccountEntity", fetch = FetchType.LAZY, optional = false)
    private UserStatEntity userStatEntity;

    @OneToMany(mappedBy = "userAccountEntity", fetch = FetchType.LAZY)
    private List<VideoEntity> videoEntityList; // 비디오 엔티티와의 관계 (추가 예시)

    @OneToMany(mappedBy = "userAccountEntity", fetch = FetchType.LAZY)
    private List<SubmissionEntity> submissionEntityList; // 제출 엔티티와의 관계 (추가 예시)

    @OneToMany(mappedBy = "userAccountEntity", fetch = FetchType.LAZY)
    private List<UserAuthEntity> userAuthEntityList; // OAuth2 인증 정보와의 관계

    public void markLogin() {
        this.lastLoginDate = LocalDate.now(); // 현재 날짜로 마지막 접속 날짜 갱신
    }

    public void modifyProfile(String nickname, String statusMessage, String profileImageUrl) {
        this.nickname = nickname;
        this.statusMessage = statusMessage;
        this.profileImageUrl = profileImageUrl;
    }
}