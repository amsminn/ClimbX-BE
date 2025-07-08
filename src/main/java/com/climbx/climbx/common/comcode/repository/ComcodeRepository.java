package com.climbx.climbx.common.comcode.repository;

import com.climbx.climbx.common.comcode.entity.ComcodeEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComcodeRepository extends JpaRepository<ComcodeEntity, String> {

    public List<ComcodeEntity> findByGroupCode(String groupCode);
}