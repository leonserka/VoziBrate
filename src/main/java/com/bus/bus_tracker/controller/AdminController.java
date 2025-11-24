package com.bus.bus_tracker.controller;

import com.bus.bus_tracker.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String adminDashboard(Model model) {
        var users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin_panel";
    }

    @PostMapping("/changeRole")
    public String changeUserRole(@RequestParam Long userId, @RequestParam String role) {
        userService.updateUserRole(userId, role);
        return "redirect:/admin";
    }
}
