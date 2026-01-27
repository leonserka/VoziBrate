package com.bus.bus_tracker.controller;

import com.bus.bus_tracker.entity.ScheduleEntity;
import com.bus.bus_tracker.service.FavoriteService;
import com.bus.bus_tracker.service.LineService;
import com.bus.bus_tracker.service.ScheduleService;
import com.bus.bus_tracker.service.ScheduleStopService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

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
    @GetMapping("/filter")
    public String filterFromHome(
            @RequestParam Long lineId,
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
        StringBuilder url = new StringBuilder("/timetable/" + lineId + "?");

        if (day != null) url.append("day=").append(day).append("&");
        if (from != null && !from.isBlank()) url.append("from=").append(from).append("&");
        if (to != null && !to.isBlank()) url.append("to=").append(to).append("&");

        String result = url.toString();
        if (result.endsWith("&") || result.endsWith("?")) {
            result = result.substring(0, result.length() - 1);
        }

        return "redirect:" + result;
    }

    @GetMapping("/{id}")
    public String lineDetails(
            @PathVariable Long id,
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime from,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime to,
            Model model
    ) {
        int selectedDay = (day != null)
                ? day
                : LocalDate.now().getDayOfWeek().getValue();

        model.addAttribute("line", lineService.getById(id));
        model.addAttribute("day", selectedDay);

        // keep filter values in the UI
        model.addAttribute("from", from);
        model.addAttribute("to", to);

        // time validation
        String timeError = null;
        if (from != null && to != null && !from.isBefore(to)) {
            timeError = "Invalid time range: 'From' must be before 'To'.";
        }
        model.addAttribute("timeError", timeError);

        var schedules = (timeError != null)
                ? java.util.List.<ScheduleEntity>of()
                : scheduleService.getSchedulesForLineAndDayFiltered(id, selectedDay, from, to);

        model.addAttribute("schedules", schedules);
        model.addAttribute("noSchedules", schedules.isEmpty());

        model.addAttribute(
                "isFavorite",
                favoriteService.isFavorite(currentUserEmail(), id)
        );

        return "timetable_line";
    }

    @GetMapping("/schedule/{scheduleId}")
    public String scheduleDetails(
            @PathVariable Long scheduleId,
            Model model
    ) {
        ScheduleEntity schedule = scheduleService.getById(scheduleId);

        model.addAttribute("schedule", schedule);
        model.addAttribute("stops", scheduleStopService.getStopsForSchedule(scheduleId));

        return "timetable_schedule";
    }

    private String currentUserEmail() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }
}

