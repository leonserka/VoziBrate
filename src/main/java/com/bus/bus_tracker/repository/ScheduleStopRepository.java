package com.bus.bus_tracker.repository;

import com.bus.bus_tracker.entity.ScheduleStopEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleStopRepository
        extends JpaRepository<ScheduleStopEntity, Long> {

    List<ScheduleStopEntity> findBySchedule_IdOrderByStopSequence(Long scheduleId);

    void deleteBySchedule_Id(Long scheduleId);
}
