package com.bus.bus_tracker.controller;

import com.bus.bus_tracker.dto.BusRequestDto;
import com.bus.bus_tracker.service.BusService;
import com.bus.bus_tracker.service.LineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/buses")
public class BusController {

    private final BusService busService;
    private final LineService lineService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("buses", busService.getAll());
        model.addAttribute("lines", lineService.getAll());
        model.addAttribute("newBus", new BusRequestDto());
        return "buses";
    }

    @PostMapping
    public String create(@ModelAttribute BusRequestDto dto) {
        busService.create(dto);
        return "redirect:/buses";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("bus", busService.getById(id));
        model.addAttribute("lines", lineService.getAll());
        return "bus_edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @ModelAttribute BusRequestDto dto) {
        busService.update(id, dto);
        return "redirect:/buses";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        busService.delete(id);
        return "redirect:/buses";
    }
}
