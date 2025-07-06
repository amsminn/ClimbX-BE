package com.climbx.climbx.user.entity;

import com.climbx.climbx.auth.enums.OAuth2ProviderType;
import com.climbx.climbx.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "user_auths")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
@Accessors(fluent = true)
@Builder
public class UserAuthEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_auth_id", updatable = false, nullable = false)
    private Long userAuthId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccountEntity userAccountEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_provider", length = 20, nullable = false)
    @NotNull
    @Size(max = 20)
    private OAuth2ProviderType oauthProvider; // OAuth2 제공자 (KAKAO, GOOGLE, APPLE)

    @Column(name = "oauth_provider_id", length = 100, nullable = false)
    @NotBlank
    @Size(max = 100)
    private String oauthProviderId; // 제공자별 고유 사용자 ID

    @Column(name = "provider_email", length = 100, nullable = true)
    @Size(max = 100)
    private String providerEmail; // 제공자에서 제공한 이메일

    @Builder.Default
    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false; // 주 인증 수단 여부

    /**
     * 주 인증 수단으로 설정합니다.
     */
    public void markAsPrimary() {
        this.isPrimary = true;
    }

    /**
     * 주 인증 수단에서 해제합니다.
     */
    public void unmarkAsPrimary() {
        this.isPrimary = false;
    }
} 