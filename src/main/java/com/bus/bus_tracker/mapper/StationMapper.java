package com.bus.bus_tracker.mapper;

import com.bus.bus_tracker.dto.StationRequestDto;
import com.bus.bus_tracker.dto.StationResponseDto;
import com.bus.bus_tracker.entity.StationEntity;
import org.springframework.stereotype.Component;

@Component
public class StationMapper {

    public StationEntity toEntity(StationRequestDto dto) {
        StationEntity e = new StationEntity();
        e.setName(dto.getName());
        e.setLocation(dto.getLocation());
        e.setGpsLat(dto.getGpsLat());
        e.setGpsLng(dto.getGpsLng());
        return e;
    }

    public void updateEntity(StationEntity entity, StationRequestDto dto) {
        entity.setName(dto.getName());
        entity.setLocation(dto.getLocation());
        entity.setGpsLat(dto.getGpsLat());
        entity.setGpsLng(dto.getGpsLng());
    }

    public StationResponseDto toResponse(StationEntity e) {
        StationResponseDto dto = new StationResponseDto();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setLocation(e.getLocation());
        dto.setGpsLat(e.getGpsLat());
        dto.setGpsLng(e.getGpsLng());
        return dto;
    }
}
