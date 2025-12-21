package com.bus.bus_tracker.controller;

import com.bus.bus_tracker.service.FavoriteService;
import com.bus.bus_tracker.service.LineService;
import com.bus.bus_tracker.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/timetable")
@RequiredArgsConstructor
public class TimetableController {

    private final LineService lineService;
    private final FavoriteService favoriteService;
    private final ScheduleService scheduleService;



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
    public String lineDetails(@PathVariable Long id, Model model) {

        int today = java.time.LocalDate.now().getDayOfWeek().getValue(); // 1–7 (Mon–Sun)

        model.addAttribute("line", lineService.getById(id));
        model.addAttribute("day", today);
        model.addAttribute(
                "schedules",
                scheduleService.getSchedulesForLineAndDay(id, today)
        );

        model.addAttribute(
                "isFavorite",
                favoriteService.isFavorite(currentUserEmail(), id)
        );

        return "timetable_line";
    }



    private String currentUserEmail() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

}
