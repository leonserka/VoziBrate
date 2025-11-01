package com.bus.bus_tracker.repository;

import com.bus.bus_tracker.entity.RouteStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteStationRepository extends JpaRepository<RouteStation, Long> {
}
