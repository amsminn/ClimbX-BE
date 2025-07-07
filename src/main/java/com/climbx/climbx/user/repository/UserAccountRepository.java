package com.climbx.climbx.user.repository;

import com.climbx.climbx.user.entity.UserAccountEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccountEntity, Long> {

    /*
     * 사용자 닉네임으로 조회, 중복 검사
     */
    Optional<UserAccountEntity> findByNickname(String nickname);

    boolean existsByNickname(String nickname);

    /*
     * 사용자 id로 조회, 중복 검사
     */
    Optional<UserAccountEntity> findByUserId(Long userId);

    /*
     * 사용자 이메일로 조회, 중복 검사
<<<<<<< HEAD
=======
     */
    Optional<UserAccountEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    /*
     * 복수 사용자 조회
>>>>>>> 8947ec5 (refactor: 인증 관련 DTO, 예외 처리, JWT 필터 및 테스트 코드 리팩토링)
     */
    Optional<UserAccountEntity> findByEmail(String email);

    // 특정 역할의 사용자들 조회
    List<UserAccountEntity> findByRole(String role);

<<<<<<< HEAD
=======
    // 특정 역할의 모든 사용자 조회
    Page<UserAccountEntity> findByRole(String role, Pageable pageable);

    // 닉네임에 특정 문자열을 포함하는 사용자들 조회 (List 반환)
    List<UserAccountEntity> findByNicknameContaining(String nickname);


>>>>>>> 3c23416 ([SWM-130] feat: replace enum with comcode)
    // 특정 역할이면서 닉네임에 특정 문자열을 포함하는 사용자들 조회
    List<UserAccountEntity> findByRoleAndNicknameContaining(String role, String nickname);
}
