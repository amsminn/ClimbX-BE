package com.climbx.climbx.user.repository;

import com.climbx.climbx.auth.enums.OAuth2ProviderType;
import com.climbx.climbx.user.entity.UserAuthEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAuthRepository extends JpaRepository<UserAuthEntity, Long> {

    /**
     * OAuth2 제공자와 제공자 ID로 사용자 인증 정보를 조회합니다.
     */
    Optional<UserAuthEntity> findByOauthProviderAndOauthProviderId(
        OAuth2ProviderType oauthProvider, 
        String oauthProviderId
    );

    /**
     * OAuth2 제공자와 제공자 ID 존재 여부를 확인합니다.
     */
    boolean existsByOauthProviderAndOauthProviderId(
        OAuth2ProviderType oauthProvider, 
        String oauthProviderId
    );

    /**
     * 특정 사용자의 모든 인증 정보를 조회합니다.
     */
    List<UserAuthEntity> findByUserAccountEntity_UserId(Long userId);

    /**
     * 특정 사용자의 주 인증 수단을 조회합니다.
     */
    Optional<UserAuthEntity> findByUserAccountEntity_UserIdAndIsPrimaryTrue(Long userId);

    /**
     * 특정 사용자의 특정 제공자 인증 정보를 조회합니다.
     */
    Optional<UserAuthEntity> findByUserAccountEntity_UserIdAndOauthProvider(
        Long userId, 
        OAuth2ProviderType oauthProvider
    );

    /**
     * 특정 사용자의 모든 주 인증 수단을 해제합니다.
     */
    @Query("UPDATE UserAuthEntity ua SET ua.isPrimary = false WHERE ua.userAccountEntity.userId = :userId")
    void unmarkAllAsPrimaryForUser(@Param("userId") Long userId);

    /**
     * 제공자별 인증 정보 개수를 조회합니다.
     */
    long countByOauthProvider(OAuth2ProviderType oauthProvider);

    /**
     * 특정 사용자의 인증 수단 개수를 조회합니다.
     */
    long countByUserAccountEntity_UserId(Long userId);
} 