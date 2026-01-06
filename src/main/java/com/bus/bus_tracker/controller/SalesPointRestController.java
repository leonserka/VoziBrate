package com.bus.bus_tracker.controller;

import com.bus.bus_tracker.entity.SalesPointEntity;
import com.bus.bus_tracker.service.SalesPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sales-points-json")
public class SalesPointRestController {

    private final SalesPointService service;

    @GetMapping
    public List<SalesPointEntity> getAll() {
        return service.getAll();
    }
}
