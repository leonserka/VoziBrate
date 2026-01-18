package com.bus.bus_tracker.controller;

import com.bus.bus_tracker.dto.RouteStopDto;
import com.bus.bus_tracker.entity.LineEntity;
import com.bus.bus_tracker.repository.LineRepository;
import com.bus.bus_tracker.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lines")
public class RouteRestController {

    private final LineRepository lineRepository;
    private final RouteService routeService;

    @GetMapping("/{lineNumber}/route")
    public List<RouteStopDto> getRouteByLineNumber(
            @PathVariable String lineNumber,
            @RequestParam(defaultValue = "A") String variant
    ) {
        LineEntity line = lineRepository
                .findByLineNumberAndVariant(lineNumber.trim(), variant.trim().toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Line not found: " + lineNumber + " variant=" + variant
                ));

        return routeService.getRoute(line.getId())
                .stream()
                .map(rs -> new RouteStopDto(
                        rs.getStation().getId(),
                        rs.getStation().getName(),
                        rs.getStation().getGpsLat(),
                        rs.getStation().getGpsLng(),
                        rs.getOrderNumber(),
                        rs.getMinutesFromStart()
                ))
                .toList();
    }
}
