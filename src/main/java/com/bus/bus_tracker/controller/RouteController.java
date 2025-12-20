package com.bus.bus_tracker.controller;

import com.bus.bus_tracker.service.LineService;
import com.bus.bus_tracker.service.RouteService;
import com.bus.bus_tracker.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/lines/{lineId}/route")
public class RouteController {

    private final RouteService routeService;
    private final LineService lineService;
    private final StationService stationService;

    @GetMapping
    public String routePage(
            @PathVariable Long lineId,
            Model model
    ) {
        model.addAttribute("line", lineService.getById(lineId));
        model.addAttribute("route", routeService.getRoute(lineId));
        model.addAttribute("stations", stationService.getAll());
        return "line_route";
    }

    @PostMapping("/add")
    public String addStation(
            @PathVariable Long lineId,
            @RequestParam Long stationId
    ) {
        routeService.addStation(lineId, stationId);
        return "redirect:/lines/" + lineId + "/route";
    }

    @PostMapping("/delete/{routeStationId}")
    public String delete(
            @PathVariable Long lineId,
            @PathVariable Long routeStationId
    ) {
        routeService.remove(routeStationId);
        return "redirect:/lines/" + lineId + "/route";
    }

    @PostMapping("/up/{routeStationId}")
    public String moveUp(
            @PathVariable Long lineId,
            @PathVariable Long routeStationId
    ) {
        routeService.moveUp(routeStationId);
        return "redirect:/lines/" + lineId + "/route";
    }

    @PostMapping("/down/{routeStationId}")
    public String moveDown(
            @PathVariable Long lineId,
            @PathVariable Long routeStationId
    ) {
        routeService.moveDown(routeStationId);
        return "redirect:/lines/" + lineId + "/route";
    }
}
