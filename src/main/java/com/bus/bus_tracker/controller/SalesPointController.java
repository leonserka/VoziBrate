package com.bus.bus_tracker.controller;

import com.bus.bus_tracker.service.SalesPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/sales-points")
public class SalesPointController {

    private final SalesPointService service;

    @GetMapping
    public String page(Model model) {
        model.addAttribute("salesPoints", service.getAll());
        return "sales_points";
    }

    @PostMapping
    public String create(@RequestParam String name,
                         @RequestParam(required = false) String address,
                         @RequestParam Double gpsLat,
                         @RequestParam Double gpsLng) {

        service.create(name, address, gpsLat, gpsLng);
        return "redirect:/admin/sales-points";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/admin/sales-points";
    }
}
