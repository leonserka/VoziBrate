package com.bus.bus_tracker.controller;

import com.bus.bus_tracker.dto.UserRegisterDto;
import com.bus.bus_tracker.dto.UserLoginDto;
import com.bus.bus_tracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("userRegisterDto", new UserRegisterDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute UserRegisterDto dto, Model model) {
        var response = userService.register(dto);
        model.addAttribute("userName", response.getName());
        return "register_success";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model, @ModelAttribute("userLoginDto") UserLoginDto userLoginDto) {
        if (userLoginDto == null) {
            model.addAttribute("userLoginDto", new UserLoginDto());
        }
        return "login";
    }

    @GetMapping("/login_success")
    public String loginSuccess(Model model) {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof UserDetails userDetails) {
            model.addAttribute("userName", userDetails.getUsername());
            model.addAttribute("role", userDetails.getAuthorities()
                    .stream()
                    .findFirst()
                    .map(Object::toString)
                    .orElse("N/A"));
        }

        return "login_success";
    }
}
