package com.bus.bus_tracker.repository;

import com.bus.bus_tracker.entity.BusPositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BusPositionRepository extends JpaRepository<BusPositionEntity, Long> {

    @Query(value = """
        SELECT DISTINCT ON (bus_id) *
        FROM bus_positions
        ORDER BY bus_id, timestamp DESC
        """, nativeQuery = true)
    List<BusPositionEntity> findLatestPerBus();

    @Modifying
    @Transactional
    @Query("DELETE FROM BusPositionEntity p WHERE p.timestamp < :cutoff")
    int deleteOlderThan(LocalDateTime cutoff);
}
