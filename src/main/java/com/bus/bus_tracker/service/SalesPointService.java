package com.bus.bus_tracker.service;

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

    public SalesPointEntity create(String name,
                                   String address,
                                   Double gpsLat,
                                   Double gpsLng) {

        SalesPointEntity sp = new SalesPointEntity();
        sp.setName(name);
        sp.setAddress(address);
        sp.setGpsLat(gpsLat);
        sp.setGpsLng(gpsLng);

        return repo.save(sp);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
