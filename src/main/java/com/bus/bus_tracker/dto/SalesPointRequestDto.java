package com.bus.bus_tracker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesPointRequestDto {
    private String name;
    private String address;
    private Double gpsLat;
    private Double gpsLng;
}
