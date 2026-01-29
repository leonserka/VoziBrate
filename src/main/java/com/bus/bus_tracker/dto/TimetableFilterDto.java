package com.bus.bus_tracker.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Getter
@Setter
public class TimetableFilterDto {

    private Long lineId;
    private Integer day;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime from;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime to;
}
