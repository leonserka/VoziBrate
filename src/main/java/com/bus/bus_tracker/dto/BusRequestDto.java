package com.bus.bus_tracker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusRequestDto {
    private String busNumber;
    private String registration;
    private String gpsId;
    private Long lineId;
}
