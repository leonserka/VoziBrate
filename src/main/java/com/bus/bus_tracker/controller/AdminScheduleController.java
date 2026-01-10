package com.bus.bus_tracker.controller;

import com.bus.bus_tracker.service.ScheduleStopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/schedules")
public class AdminScheduleController {

    private final ScheduleStopService scheduleStopService;

    // GLOBAL: regenerira schedule_stops za sve schedules u bazi
    @PostMapping("/regenerate-stops")
    @ResponseBody
    public ResponseEntity<String> regenerateAll() {
        scheduleStopService.regenerateStopsForAllSchedules();
        return ResponseEntity.ok("OK: regenerated stops for ALL schedules");
    }
}
