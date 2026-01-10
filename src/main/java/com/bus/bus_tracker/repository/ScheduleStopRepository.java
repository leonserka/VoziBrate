package com.bus.bus_tracker.repository;

import com.bus.bus_tracker.entity.ScheduleStopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleStopRepository extends JpaRepository<ScheduleStopEntity, Long> {

    List<ScheduleStopEntity> findBySchedule_IdOrderByStopSequence(Long scheduleId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from ScheduleStopEntity s where s.schedule.id = :scheduleId")
    void bulkDeleteByScheduleId(@Param("scheduleId") Long scheduleId);
}
