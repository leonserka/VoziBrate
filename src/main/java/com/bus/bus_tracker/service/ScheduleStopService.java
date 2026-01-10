package com.bus.bus_tracker.service;

import com.bus.bus_tracker.entity.RouteStationEntity;
import com.bus.bus_tracker.entity.ScheduleEntity;
import com.bus.bus_tracker.entity.ScheduleStopEntity;
import com.bus.bus_tracker.repository.RouteStationRepository;
import com.bus.bus_tracker.repository.ScheduleRepository;
import com.bus.bus_tracker.repository.ScheduleStopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScheduleStopService {

    private final ScheduleStopRepository scheduleStopRepository;
    private final ScheduleRepository scheduleRepository;
    private final RouteStationRepository routeStationRepository;

    // Spring proxy (da @Transactional radi i kad zoveš iz iste klase)
    private final ObjectProvider<ScheduleStopService> self;

    public List<ScheduleStopEntity> getStopsForSchedule(Long scheduleId) {
        return scheduleStopRepository.findBySchedule_IdOrderByStopSequence(scheduleId);
    }

    // Svaki schedule u svojoj transakciji
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void regenerateStopsForSchedule(ScheduleEntity schedule, List<RouteStationEntity> route) {
        Long scheduleId = schedule.getId();
        if (scheduleId == null) {
            throw new IllegalArgumentException("Schedule id is null (not persisted?)");
        }

        // 1) pobriši stare
        scheduleStopRepository.bulkDeleteByScheduleId(scheduleId);

        // 2) pripremi nove u memoriji
        List<ScheduleStopEntity> stops = new ArrayList<>(route.size());
        int seq = 1;

        for (RouteStationEntity rs : route) {
            LocalTime stopTime = schedule.getDeparture()
                    .plusMinutes(rs.getMinutesFromStart());

            boolean isNextDay = schedule.isArrivalNextDay()
                    && stopTime.isBefore(schedule.getDeparture());

            ScheduleStopEntity stop = new ScheduleStopEntity();
            stop.setSchedule(schedule);
            stop.setStation(rs.getStation());
            stop.setStopSequence(seq++);
            stop.setTime(stopTime);
            stop.setNextDay(isNextDay);

            stops.add(stop);
        }

        // 3) jedan batch (uz Hibernate batching u properties još bolje)
        scheduleStopRepository.saveAll(stops);
    }

    public void regenerateStopsForLine(Long lineId) {
        List<ScheduleEntity> schedules = scheduleRepository.findByLine_Id(lineId);

        // route je ista za sve scheduleove te linije -> jedan query
        List<RouteStationEntity> route = routeStationRepository.findRouteWithStations(lineId);

        for (ScheduleEntity s : schedules) {
            self.getObject().regenerateStopsForSchedule(s, route);
        }
    }

    public void regenerateStopsForAllSchedules() {
        List<ScheduleEntity> all = scheduleRepository.findAll();

        // cache ruta po lineId (da ne radiš isti query 100 puta)
        Map<Long, List<RouteStationEntity>> routeCache = new HashMap<>();

        for (ScheduleEntity s : all) {
            Long lineId = s.getLine().getId();

            List<RouteStationEntity> route = routeCache.computeIfAbsent(
                    lineId,
                    routeStationRepository::findRouteWithStations
            );

            self.getObject().regenerateStopsForSchedule(s, route);
        }
    }
}
