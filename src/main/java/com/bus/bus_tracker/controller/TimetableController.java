package com.bus.bus_tracker.controller;

import com.bus.bus_tracker.service.FavoriteService;
import com.bus.bus_tracker.service.LineService;
import com.bus.bus_tracker.service.ScheduleService;
import com.bus.bus_tracker.service.ScheduleStopService;
import com.bus.bus_tracker.entity.ScheduleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@Controller
@RequestMapping("/timetable")
@RequiredArgsConstructor
public class TimetableController {

    private final LineService lineService;
    private final FavoriteService favoriteService;
    private final ScheduleService scheduleService;
    private final ScheduleStopService scheduleStopService;



    @GetMapping
    public String timetable(
            @RequestParam(required = false) String q,
            Model model
    ) {
        if (q == null || q.isBlank()) {
            model.addAttribute("lines", lineService.getAll());
        } else {
            model.addAttribute("lines", lineService.search(q));
        }

        model.addAttribute("query", q);
        return "timetable";
    }

    @GetMapping("/{id}")
    public String lineDetails(
            @PathVariable Long id,
            @RequestParam(required = false) Integer day,
            Model model
    ) {
        int selectedDay = (day != null) ? day : LocalDate.now().getDayOfWeek().getValue();

        model.addAttribute("line", lineService.getById(id));
        model.addAttribute("day", selectedDay);

        model.addAttribute(
                "schedules",
                scheduleService.getSchedulesForLineAndDay(id, selectedDay)
        );

        model.addAttribute(
                "isFavorite",
                favoriteService.isFavorite(currentUserEmail(), id)
        );

        return "timetable_line";
    }


    @GetMapping("/schedule/{scheduleId}")
    public String scheduleDetails(@PathVariable Long scheduleId, Model model) {

        ScheduleEntity schedule = scheduleService.getById(scheduleId);

        model.addAttribute("schedule", schedule);
        model.addAttribute(
                "stops",
                scheduleStopService.getStopsForSchedule(scheduleId)
        );

        return "timetable_schedule";
    }

    private String currentUserEmail() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

}
