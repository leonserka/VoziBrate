package com.bus.bus_tracker.service;

import com.bus.bus_tracker.entity.RouteStationEntity;
import com.bus.bus_tracker.entity.ScheduleEntity;
import com.bus.bus_tracker.entity.ScheduleStopEntity;
import com.bus.bus_tracker.repository.RouteStationRepository;
import com.bus.bus_tracker.repository.ScheduleRepository;
import com.bus.bus_tracker.repository.ScheduleStopRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleStopService {

    private final ScheduleStopRepository scheduleStopRepository;
    private final ScheduleRepository scheduleRepository;
    private final RouteStationRepository routeStationRepository;

    public List<ScheduleStopEntity> getStopsForSchedule(Long scheduleId) {
        return scheduleStopRepository
                .findByScheduleIdOrderByStopSequence(scheduleId);
    }

    public void regenerateStopsForSchedule(ScheduleEntity schedule) {

        // 1. obriši stare
        scheduleStopRepository.deleteBySchedule_Id(schedule.getId());

        scheduleStopRepository.flush();

        // 2. ruta linije
        List<RouteStationEntity> route =
                routeStationRepository.findRouteWithStations(
                        schedule.getLine().getId()
                );

        LocalTime time = schedule.getDeparture();
        int seq = 1;

        for (RouteStationEntity rs : route) {

            ScheduleStopEntity stop = new ScheduleStopEntity();
            stop.setSchedule(schedule);
            stop.setStation(rs.getStation());
            stop.setStopSequence(seq++);
            stop.setTime(time);

            scheduleStopRepository.save(stop);

            time = time.plusMinutes(5);
        }
    }


    public void regenerateStopsForLine(Long lineId) {

        List<ScheduleEntity> schedules =
                scheduleRepository.findByLine_Id(lineId);

        for (ScheduleEntity s : schedules) {
            regenerateStopsForSchedule(s);
        }
    }

    public void regenerateStopsForAllSchedules() {

        List<ScheduleEntity> all = scheduleRepository.findAll();

        for (ScheduleEntity s : all) {
            regenerateStopsForSchedule(s);
        }
    }
}
