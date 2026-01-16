package com.bus.bus_tracker.service;

import com.bus.bus_tracker.dto.SalesPointRequestDto;
import com.bus.bus_tracker.entity.SalesPointEntity;
import com.bus.bus_tracker.repository.SalesPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesPointService {

    private final SalesPointRepository repo;

    public List<SalesPointEntity> getAll() {
        return repo.findAll();
    }

    public SalesPointEntity create(SalesPointRequestDto dto) {
        SalesPointEntity sp = new SalesPointEntity();
        sp.setName(dto.getName());
        sp.setAddress(dto.getAddress());
        sp.setGpsLat(dto.getGpsLat());
        sp.setGpsLng(dto.getGpsLng());

        return repo.save(sp);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
