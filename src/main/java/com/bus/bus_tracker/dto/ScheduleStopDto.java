package com.bus.bus_tracker.dto;

public record ScheduleStopDto(
        Long stationId,
        String name,
        Double lat,
        Double lng,
        Integer stopSequence,
        String time,
        boolean nextDay
) {}
