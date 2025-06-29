package com.climbx.climbx.problem.repository;

import com.climbx.climbx.problem.entity.ProblemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<ProblemEntity, Long> {

    /*
     * 유저 api 구현에 필요한 메서드들만 임시 구현.
     * 추후 구현 예정
     */

}
