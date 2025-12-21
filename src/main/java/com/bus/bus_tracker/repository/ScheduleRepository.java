package com.bus.bus_tracker.repository;

import com.bus.bus_tracker.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {

    List<ScheduleEntity> findByLine_IdAndDayOfWeekOrderByDeparture(
            Long lineId,
            Integer dayOfWeek
    );
}
