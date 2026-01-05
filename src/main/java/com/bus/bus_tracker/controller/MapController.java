package com.bus.bus_tracker.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapController {
    @GetMapping("/live")
    public String liveMap() {
        return "live_map";
    }
}