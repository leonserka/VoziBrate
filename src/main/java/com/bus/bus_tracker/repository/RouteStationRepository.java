package com.bus.bus_tracker.repository;

import com.bus.bus_tracker.entity.RouteStationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteStationRepository extends JpaRepository<RouteStationEntity, Long> {
}
