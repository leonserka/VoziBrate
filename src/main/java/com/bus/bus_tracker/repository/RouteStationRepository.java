package com.bus.bus_tracker.repository;

import com.bus.bus_tracker.entity.RouteStationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteStationRepository
        extends JpaRepository<RouteStationEntity, Long> {

    @Query("""
        select rs
        from RouteStationEntity rs
        join fetch rs.station
        where rs.line.id = :lineId
        order by rs.orderNumber
    """)
    List<RouteStationEntity> findRouteWithStations(@Param("lineId") Long lineId);

    boolean existsByLine_IdAndStation_Id(Long lineId, Long stationId);
}
