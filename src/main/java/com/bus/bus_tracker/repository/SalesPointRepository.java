package com.bus.bus_tracker.repository;

import com.bus.bus_tracker.entity.SalesPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesPointRepository extends JpaRepository<SalesPoint, Long> {
}
