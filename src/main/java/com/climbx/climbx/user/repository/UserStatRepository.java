package com.climbx.climbx.user.repository;

import com.climbx.climbx.user.entity.UserStat;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatRepository extends JpaRepository<UserStat, Long> {

    /*
     * 사용자 통계 단일 조회 / 존재 검사
     */
    Optional<UserStat> findByUserId(Long userId);
    boolean existsByUserId(Long userId);

    Page<UserStat> findAllByOrderByRatingDesc(Pageable pageable);

    Page<UserStat> findAllByOrderByLongestStreakDesc(Pageable pageable);

    Page<UserStat> findAllByOrderByCurrentStreakDesc(Pageable pageable);

    Page<UserStat> findAllByOrderByRivalCountDesc(Pageable pageable);

    Page<UserStat> findByRatingGreaterThanEqualOrderByRatingDesc(Long minRating, Pageable pageable);

    /**
     * 특정 레이팅을 가진 사용자의 순위(1-based) 조회
     */
    long countByRatingGreaterThan(Long rating);
    default Long findRatingRank(Long rating) {
        return countByRatingGreaterThan(rating) + 1;
    }
}