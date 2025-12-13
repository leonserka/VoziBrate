package com.bus.bus_tracker.controller;

import com.bus.bus_tracker.dto.LineRequestDto;
import com.bus.bus_tracker.dto.LineResponseDto;
import com.bus.bus_tracker.service.LineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/lines")
public class LineController {

    private final LineService service;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("lines", service.getAll());
        model.addAttribute("newLine", new LineRequestDto());
        return "lines";
    }

    @PostMapping
    public String create(@ModelAttribute LineRequestDto dto) {
        service.create(dto);
        return "redirect:/lines";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("line", service.getById(id));
        return "line_edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @ModelAttribute LineRequestDto dto) {
        service.update(id, dto);
        return "redirect:/lines";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/lines";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam String q,
            Model model
    ) {
        model.addAttribute("lines", service.search(q));
        model.addAttribute("query", q);
        model.addAttribute("newLine", new LineRequestDto());
        return "lines";
    }


}
