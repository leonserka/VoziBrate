package com.bus.bus_tracker.service;

import com.bus.bus_tracker.dto.BusRequestDto;
import com.bus.bus_tracker.dto.BusResponseDto;
import com.bus.bus_tracker.entity.BusEntity;
import com.bus.bus_tracker.entity.LineEntity;
import com.bus.bus_tracker.mapper.BusMapper;
import com.bus.bus_tracker.repository.BusRepository;
import com.bus.bus_tracker.repository.LineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusService {

    private final BusRepository repo;
    private final LineRepository lineRepo;
    private final BusMapper mapper;

    public List<BusResponseDto> getAll() {
        return repo.findAll().stream().map(mapper::toResponse).toList();
    }

    public BusResponseDto getById(Long id) {
        BusEntity e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Bus not found"));
        return mapper.toResponse(e);
    }

    public void create(BusRequestDto dto) {
        BusEntity e = mapper.toEntity(dto);
        if (dto.getLineId() != null) {
            LineEntity line = lineRepo.findById(dto.getLineId()).orElseThrow(() -> new IllegalArgumentException("Line not found"));
            e.setLine(line);
        }
        repo.save(e);
    }

    public void update(Long id, BusRequestDto dto) {
        BusEntity e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Bus not found"));
        mapper.updateEntity(e, dto);
        if (dto.getLineId() != null) {
            LineEntity line = lineRepo.findById(dto.getLineId()).orElseThrow(() -> new IllegalArgumentException("Line not found"));
            e.setLine(line);
        } else {
            e.setLine(null);
        }
        repo.save(e);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
