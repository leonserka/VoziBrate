package com.bus.bus_tracker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StationResponseDto {
    private Long id;
    private String name;
    private String location;
    private Double gpsLat;
    private Double gpsLng;
}
