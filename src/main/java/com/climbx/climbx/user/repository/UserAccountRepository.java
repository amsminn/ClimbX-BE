package com.climbx.climbx.user.repository;

import com.climbx.climbx.user.entity.UserAccount;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    /*
     * 사용자 닉네임으로 조회, 중복 검사
     */
    Optional<UserAccount> findByNickname(String nickname);

    boolean existsByNicknameIgnoreCase(String nickname);
    boolean existsByNickname(String nickname);

    /*
     * 사용자 id로 조회, 중복 검사
     */
    Optional<UserAccount> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    /*
     * 복수 사용자 조회
     */
    Page<UserAccount> findByLastLoginDateAfter(LocalDate since, Pageable pageable);

    Page<UserAccount> findAllByOrderByLastLoginDateDesc(Pageable pageable);

    // 사용자 닉네임으로 조회 (대소문자 구분 없이)
    Page<UserAccount> findByNicknameContainingIgnoreCase(String nickname, Pageable pageable);

    // 특정 역할의 사용자들 조회
    Page<UserAccount> findByRole(String role, Pageable pageable);
}
