package com.bus.bus_tracker.service;

import com.bus.bus_tracker.dto.LineRequestDto;
import com.bus.bus_tracker.dto.LineResponseDto;
import com.bus.bus_tracker.entity.LineEntity;
import com.bus.bus_tracker.mapper.LineMapper;
import com.bus.bus_tracker.repository.LineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.bus.bus_tracker.specification.LineSpecification;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LineService {

    private final LineRepository repo;
    private final LineMapper mapper;

    public List<LineResponseDto> getAll() {
        return repo.findAll().stream().map(mapper::toResponse).toList();
    }

    public LineResponseDto getById(Long id) {
        LineEntity e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Line not found"));
        return mapper.toResponse(e);
    }

    public void create(LineRequestDto dto) {
        repo.save(mapper.toEntity(dto));
    }

    public void update(Long id, LineRequestDto dto) {
        LineEntity e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Line not found"));
        mapper.updateEntity(e, dto);
        repo.save(e);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<LineResponseDto> search(String query) {
        return repo
                .findAll(LineSpecification.search(query))
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

}
