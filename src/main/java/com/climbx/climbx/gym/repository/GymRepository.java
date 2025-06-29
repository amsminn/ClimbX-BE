package com.climbx.climbx.gym.repository;

import com.climbx.climbx.gym.entity.GymEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GymRepository extends JpaRepository<GymEntity, Long> {

    @Query(value =
        "SELECT *, "
            + "(6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * cos(radians(longitude) - "
            + "radians(:longitude)) + sin(radians(:latitude)) * sin(radians(latitude)))) AS distance "
            + "FROM gyms ORDER BY distance ASC",
        nativeQuery = true
    )
    List<GymEntity> findAllByLocationOrderByDistance(
        @Param("latitude") Double latitude,
        @Param("longitude") Double longitude
    );

    @Query(value =
        "SELECT *, "
            + "(6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * cos(radians(longitude) - "
            + "radians(:longitude)) + sin(radians(:latitude)) * sin(radians(latitude)))) AS distance "
            + "FROM gyms "
            + "WHERE LOWER(name) LIKE CONCAT('%', LOWER(:keyword), '%') "
            + "ORDER BY distance ASC",
        nativeQuery = true
    )
    List<GymEntity> findAllByNameContainingIgnoreCaseOrderByDistance(
        @Param("latitude") Double latitude,
        @Param("longitude") Double longitude,
        @Param("keyword") String keyword
    );

    List<GymEntity> findAllByNameContainingIgnoreCase(String keyword);
}
