package com.bus.bus_tracker.service;

import com.bus.bus_tracker.entity.RouteStationEntity;
import com.bus.bus_tracker.repository.LineRepository;
import com.bus.bus_tracker.repository.RouteStationRepository;
import com.bus.bus_tracker.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteStationRepository routeRepo;
    private final LineRepository lineRepo;
    private final StationRepository stationRepo;
    private final ScheduleStopService scheduleStopService;

    @Transactional(readOnly = true)
    public List<RouteStationEntity> getRoute(Long lineId) {
        return routeRepo.findRouteWithStations(lineId);
    }

    @Transactional
    public void addStation(Long lineId, Long stationId) {

        if (routeRepo.existsByLine_IdAndStation_Id(lineId, stationId)) {
            return;
        }

        int nextOrder = routeRepo.findRouteWithStations(lineId).size() + 1;

        RouteStationEntity rs = new RouteStationEntity();
        rs.setLine(lineRepo.getReferenceById(lineId));
        rs.setStation(stationRepo.getReferenceById(stationId));
        rs.setOrderNumber(nextOrder);

        routeRepo.save(rs);

        scheduleStopService.regenerateStopsForLine(lineId);
    }

    @Transactional
    public void remove(Long routeStationId) {

        RouteStationEntity rs = routeRepo.findById(routeStationId)
                .orElseThrow(() -> new IllegalArgumentException("Route station not found"));

        Long lineId = rs.getLine().getId();

        routeRepo.delete(rs);

        scheduleStopService.regenerateStopsForLine(lineId);
    }

    @Transactional
    public void moveUp(Long id) {
        swap(id, -1);
    }

    @Transactional
    public void moveDown(Long id) {
        swap(id, +1);
    }

    private void swap(Long id, int dir) {

        RouteStationEntity current = routeRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Route station not found: " + id));

        Long lineId = current.getLine().getId();
        int newOrder = current.getOrderNumber() + dir;

        if (newOrder < 1) return;

        RouteStationEntity other = routeRepo.findRouteWithStations(lineId)
                .stream()
                .filter(rs -> rs.getOrderNumber().equals(newOrder))
                .findFirst()
                .orElse(null);

        if (other == null) return;

        int oldOrder = current.getOrderNumber();
        current.setOrderNumber(newOrder);
        other.setOrderNumber(oldOrder);

        routeRepo.save(current);
        routeRepo.save(other);

        scheduleStopService.regenerateStopsForLine(lineId);
    }
}
