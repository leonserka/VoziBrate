package com.bus.bus_tracker.service;

import com.bus.bus_tracker.entity.ScheduleEntity;
import com.bus.bus_tracker.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public List<ScheduleEntity> getSchedulesForLineAndDay(Long lineId, int dayOfWeek) {
        return scheduleRepository.findByLine_IdAndDayOfWeekOrderByDeparture(
                lineId,
                dayOfWeek
        );
    }

    public ScheduleEntity getById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Schedule not found: " + id)
                );
    }
}
