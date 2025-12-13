package com.bus.bus_tracker.controller;

import com.bus.bus_tracker.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    private String currentUserEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    @GetMapping
    public String favorites(Model model) {
        model.addAttribute("favorites", favoriteService.getUserFavorites(currentUserEmail()));
        return "favorites";
    }

    @PostMapping("/toggle/{lineId}")
    public String toggle(@PathVariable Long lineId,
                         @RequestHeader(value = "Referer", required = false) String referer) {
        favoriteService.toggleFavorite(currentUserEmail(), lineId);
        return "redirect:" + (referer != null ? referer : "/timetable");
    }

    @PostMapping("/remove/{lineId}")
    public String remove(@PathVariable Long lineId) {
        favoriteService.removeFavorite(currentUserEmail(), lineId);
        return "redirect:/favorites";
    }
}
