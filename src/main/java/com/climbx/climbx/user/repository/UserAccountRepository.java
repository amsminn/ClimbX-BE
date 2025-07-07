package com.climbx.climbx.user.repository;

import com.climbx.climbx.common.enums.RoleType;
import com.climbx.climbx.user.entity.UserAccountEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccountEntity, Long> {

    /*
     * 사용자 닉네임으로 조회, 중복 검사
     */
    Optional<UserAccountEntity> findByNickname(String nickname);

    boolean existsByNicknameIgnoreCase(String nickname);

    boolean existsByNickname(String nickname);

    /*
     * 사용자 id로 조회, 중복 검사
     */
    Optional<UserAccountEntity> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    /*
     * 사용자 이메일로 조회, 중복 검사
     */
    Optional<UserAccountEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    /*
     * 복수 사용자 조회
     */
    Page<UserAccountEntity> findByLastLoginDateAfter(LocalDate since, Pageable pageable);

    Page<UserAccountEntity> findAllByOrderByLastLoginDateDesc(Pageable pageable);

    // 사용자 닉네임으로 조회 (대소문자 구분 없이)
    Page<UserAccountEntity> findByNicknameContainingIgnoreCase(String nickname, Pageable pageable);

    // 특정 역할의 사용자들 조회
    List<UserAccountEntity> findByRole(RoleType role);

    // 특정 역할의 모든 사용자 조회
    Page<UserAccountEntity> findByRole(RoleType role, Pageable pageable);

    // 닉네임에 특정 문자열을 포함하는 사용자들 조회 (List 반환)
    List<UserAccountEntity> findByNicknameContaining(String nickname);


    // 특정 역할이면서 닉네임에 특정 문자열을 포함하는 사용자들 조회
    List<UserAccountEntity> findByRoleAndNicknameContaining(RoleType role, String nickname);

    List<UserAccountEntity> findByNicknameIn(List<String> nicknames);
}
