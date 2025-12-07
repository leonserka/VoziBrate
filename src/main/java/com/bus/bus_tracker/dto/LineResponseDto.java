package com.bus.bus_tracker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LineResponseDto {
    private Long id;
    private String lineNumber;
    private String name;
    private String direction;
}
