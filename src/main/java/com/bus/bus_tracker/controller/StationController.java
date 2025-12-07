package com.bus.bus_tracker.controller;

import com.bus.bus_tracker.dto.StationRequestDto;
import com.bus.bus_tracker.dto.StationResponseDto;
import com.bus.bus_tracker.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/stations")
public class StationController {

    private final StationService stationService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("stations", stationService.getAll());
        model.addAttribute("newStation", new StationRequestDto());
        return "stations";
    }

    @PostMapping
    public String create(@ModelAttribute StationRequestDto dto) {
        stationService.create(dto);
        return "redirect:/stations";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        StationResponseDto station = stationService.getById(id);
        model.addAttribute("station", station);
        return "station_edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @ModelAttribute StationRequestDto dto) {
        stationService.update(id, dto);
        return "redirect:/stations";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        stationService.delete(id);
        return "redirect:/stations";
    }
}
