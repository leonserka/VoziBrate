package com.bus.bus_tracker.mapper;

import com.bus.bus_tracker.dto.BusRequestDto;
import com.bus.bus_tracker.dto.BusResponseDto;
import com.bus.bus_tracker.entity.BusEntity;
import org.springframework.stereotype.Component;

@Component
public class BusMapper {

    public BusEntity toEntity(BusRequestDto dto) {
        BusEntity e = new BusEntity();
        e.setBusNumber(dto.getBusNumber());
        e.setRegistration(dto.getRegistration());
        e.setGpsId(dto.getGpsId());
        return e;
    }

    public void updateEntity(BusEntity e, BusRequestDto dto) {
        e.setBusNumber(dto.getBusNumber());
        e.setRegistration(dto.getRegistration());
        e.setGpsId(dto.getGpsId());
    }

    public BusResponseDto toResponse(BusEntity e) {
        BusResponseDto dto = new BusResponseDto();
        dto.setId(e.getId());
        dto.setBusNumber(e.getBusNumber());
        dto.setRegistration(e.getRegistration());
        dto.setGpsId(e.getGpsId());
        if (e.getLine() != null) {
            dto.setLineId(e.getLine().getId());
            dto.setLineName(e.getLine().getName());
        }
        return dto;
    }
}
