package com.bus.bus_tracker.dto;

public record RouteStopDto(
        Long stationId,
        String name,
        Double lat,
        Double lng,
        Integer orderNumber,
        Integer minutesFromStart
) {}
