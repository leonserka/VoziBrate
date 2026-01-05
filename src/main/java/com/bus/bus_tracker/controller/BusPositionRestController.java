package com.bus.bus_tracker.controller;

import com.bus.bus_tracker.entity.BusPositionEntity;
import com.bus.bus_tracker.repository.BusPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class BusPositionRestController {

    private final BusPositionRepository repo;

    @GetMapping("/current")
    public List<BusPositionEntity> getCurrentPositions() {
        // Vraća zadnje poznate pozicije svih buseva
        // (Za seminar je ovo OK, u praksi bi filtrirao samo one od zadnjih 5 min)
        return repo.findAll();
    }
}