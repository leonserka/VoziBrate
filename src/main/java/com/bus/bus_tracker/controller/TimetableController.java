package com.bus.bus_tracker.controller;

import com.bus.bus_tracker.dto.TimetableFilterDto;
import com.bus.bus_tracker.entity.ScheduleEntity;
import com.bus.bus_tracker.service.FavoriteService;
import com.bus.bus_tracker.service.LineService;
import com.bus.bus_tracker.service.ScheduleService;
import com.bus.bus_tracker.service.ScheduleStopService;
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


    @GetMapping("/filter")
    public String filterFromHome(TimetableFilterDto filter) {
        if (filter.getLineId() == null) {

            return "redirect:/timetable";
        }

        StringBuilder url = new StringBuilder("/timetable/" + filter.getLineId());
        String sep = "?";

        if (filter.getDay() != null) {
            url.append(sep).append("day=").append(filter.getDay());
            sep = "&";
        }

        if (filter.getFrom() != null) {
            url.append(sep).append("from=").append(filter.getFrom());
            sep = "&";
        }

        if (filter.getTo() != null) {
            url.append(sep).append("to=").append(filter.getTo());
        }

        return "redirect:" + url;
    }


    @GetMapping("/{id}")
    public String lineDetails(
            @PathVariable Long id,
            TimetableFilterDto filter,
            Model model
    ) {
        int selectedDay = (filter.getDay() != null)
                ? filter.getDay()
                : LocalDate.now().getDayOfWeek().getValue();

        model.addAttribute("line", lineService.getById(id));
        model.addAttribute("day", selectedDay);

        // keep filter values in UI
        model.addAttribute("from", filter.getFrom());
        model.addAttribute("to", filter.getTo());

        // validation
        String timeError = null;
        if (filter.getFrom() != null && filter.getTo() != null &&
                !filter.getFrom().isBefore(filter.getTo())) {
            timeError = "Invalid time range: 'From' must be before 'To'.";
        }
        model.addAttribute("timeError", timeError);

        var schedules = (timeError != null)
                ? java.util.List.<ScheduleEntity>of()
                : scheduleService.getSchedulesForLineAndDayFiltered(
                id,
                selectedDay,
                filter.getFrom(),
                filter.getTo()
        );

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

