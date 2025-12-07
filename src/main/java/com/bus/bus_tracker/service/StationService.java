package com.bus.bus_tracker.service;

import com.bus.bus_tracker.dto.StationRequestDto;
import com.bus.bus_tracker.dto.StationResponseDto;
import com.bus.bus_tracker.entity.StationEntity;
import com.bus.bus_tracker.mapper.StationMapper;
import com.bus.bus_tracker.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository repo;
    private final StationMapper mapper;

    public List<StationResponseDto> getAll() {
        return repo.findAll().stream().map(mapper::toResponse).toList();
    }

    public StationResponseDto getById(Long id) {
        StationEntity e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Station not found"));
        return mapper.toResponse(e);
    }

    public StationResponseDto create(StationRequestDto dto) {
        StationEntity e = mapper.toEntity(dto);
        return mapper.toResponse(repo.save(e));
    }

    public void update(Long id, StationRequestDto dto) {
        StationEntity e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Station not found"));
        mapper.updateEntity(e, dto);
        mapper.toResponse(repo.save(e));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
