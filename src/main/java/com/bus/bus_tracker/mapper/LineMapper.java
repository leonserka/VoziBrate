package com.bus.bus_tracker.mapper;

import com.bus.bus_tracker.dto.LineRequestDto;
import com.bus.bus_tracker.dto.LineResponseDto;
import com.bus.bus_tracker.entity.LineEntity;
import org.springframework.stereotype.Component;

@Component
public class LineMapper {

    public LineEntity toEntity(LineRequestDto dto) {
        LineEntity e = new LineEntity();
        e.setLineNumber(dto.getLineNumber());
        e.setName(dto.getName());
        e.setDirection(dto.getDirection());
        return e;
    }

    public void updateEntity(LineEntity e, LineRequestDto dto) {
        e.setLineNumber(dto.getLineNumber());
        e.setName(dto.getName());
        e.setDirection(dto.getDirection());
    }

    public LineResponseDto toResponse(LineEntity e) {
        LineResponseDto dto = new LineResponseDto();
        dto.setId(e.getId());
        dto.setLineNumber(e.getLineNumber());
        dto.setName(e.getName());
        dto.setDirection(e.getDirection());
        return dto;
    }
}
