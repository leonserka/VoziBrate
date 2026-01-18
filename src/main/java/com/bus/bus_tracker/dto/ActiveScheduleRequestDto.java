package com.bus.bus_tracker.dto;

public record ActiveScheduleRequestDto(
        String lineNumber,
        String variant,
        Double progressMin
) {}
