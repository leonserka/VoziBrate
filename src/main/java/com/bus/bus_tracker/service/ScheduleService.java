package com.bus.bus_tracker.service;

import com.bus.bus_tracker.entity.ScheduleEntity;
import com.bus.bus_tracker.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public List<ScheduleEntity> getSchedulesForLineAndDay(Long lineId, int dayOfWeek) {
        return scheduleRepository.findByLine_IdAndDayOfWeekOrderByDeparture(lineId, dayOfWeek);
    }

    // ✅ NOVO: filtriranje po vremenu
    public List<ScheduleEntity> getSchedulesForLineAndDayFiltered(
            Long lineId,
            int dayOfWeek,
            LocalTime from,
            LocalTime to
    ) {
        if (from != null && to != null) {
            return scheduleRepository.findByLine_IdAndDayOfWeekAndDepartureBetweenOrderByDeparture(
                    lineId, dayOfWeek, from, to
            );
        }

        return scheduleRepository.findByLine_IdAndDayOfWeekOrderByDeparture(lineId, dayOfWeek);
    }

    public ScheduleEntity getById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found: " + id));
    }
}

