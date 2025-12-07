package com.bus.bus_tracker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusResponseDto {
    private Long id;
    private String busNumber;
    private String registration;
    private String gpsId;
    private Long lineId;
    private String lineName;
}
