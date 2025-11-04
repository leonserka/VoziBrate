package com.bus.bus_tracker.repository;

import com.bus.bus_tracker.entity.BusPositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusPositionRepository extends JpaRepository<BusPositionEntity, Long> {
}
