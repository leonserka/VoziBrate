package com.bus.bus_tracker.exception;

import com.bus.bus_tracker.dto.UserRegisterDto;
import com.bus.bus_tracker.dto.UserLoginDto;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgs(IllegalArgumentException ex, Model model) {
        if (ex.getMessage().toLowerCase().contains("password")
                || ex.getMessage().toLowerCase().contains("email")
                || ex.getMessage().toLowerCase().contains("exists")) {

            model.addAttribute("error", ex.getMessage());
            model.addAttribute("userRegisterDto", new UserRegisterDto());
            return "register";
        }

        model.addAttribute("error", ex.getMessage());
        model.addAttribute("userLoginDto", new UserLoginDto());
        return "login";
    }

    @ExceptionHandler(Exception.class)
    public String handleOtherExceptions(Exception ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }
}