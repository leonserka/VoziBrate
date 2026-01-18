package com.bus.bus_tracker.controller;

import com.bus.bus_tracker.dto.ActiveScheduleRequestDto;
import com.bus.bus_tracker.dto.ActiveScheduleResponseDto;
import com.bus.bus_tracker.dto.ScheduleStopDto;
import com.bus.bus_tracker.entity.LineEntity;
import com.bus.bus_tracker.entity.ScheduleEntity;
import com.bus.bus_tracker.entity.ScheduleStopEntity;
import com.bus.bus_tracker.repository.LineRepository;
import com.bus.bus_tracker.service.ScheduleService;
import com.bus.bus_tracker.service.ScheduleStopService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/live")
public class LiveScheduleRestController {

    private final LineRepository lineRepository;
    private final ScheduleService scheduleService;
    private final ScheduleStopService scheduleStopService;

    @PostMapping("/active-schedule")
    public ActiveScheduleResponseDto findActiveSchedule(
            @RequestBody ActiveScheduleRequestDto req
    ) {
        String lineNumber = req.lineNumber() == null ? "" : req.lineNumber().trim();
        String variant = (req.variant() == null || req.variant().isBlank())
                ? "A"
                : req.variant().trim().toUpperCase();

        if (lineNumber.isBlank()) {
            throw new IllegalArgumentException("lineNumber is required");
        }
        if (req.progressMin() == null) {
            throw new IllegalArgumentException("progressMin is required");
        }

        double progressMin = req.progressMin();

        LineEntity line = lineRepository
                .findByLineNumberAndVariant(lineNumber, variant)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Line not found: " + lineNumber + " variant=" + variant
                        )
                );

        LocalDate today = LocalDate.now();
        int dow = today.getDayOfWeek().getValue();

        List<ScheduleEntity> schedules =
                scheduleService.getSchedulesForLineAndDay(line.getId(), dow);

        if (schedules.isEmpty()) {
            throw new IllegalArgumentException(
                    "No schedules for line=" + lineNumber +
                            " variant=" + variant +
                            " dow=" + dow
            );
        }

        LocalDateTime now = LocalDateTime.now();

        Optional<ScheduleEntity> best = schedules.stream()
                .map(s -> new ScoredSchedule(s, scoreSchedule(now, today, s, progressMin)))
                .filter(x -> x.score != null)
                .min(Comparator.comparingDouble(x -> x.score))
                .map(x -> x.schedule);

        ScheduleEntity chosen = best.orElseGet(() ->
                schedules.stream()
                        .min(Comparator.comparingLong(s ->
                                Math.abs(
                                        Duration.between(
                                                LocalDateTime.of(today, s.getDeparture()),
                                                now
                                        ).toMinutes()
                                )
                        ))
                        .orElseThrow()
        );

        return new ActiveScheduleResponseDto(chosen.getId());
    }

    @GetMapping("/schedules/{scheduleId}/stops")
    public List<ScheduleStopDto> getScheduleStops(@PathVariable Long scheduleId) {
        List<ScheduleStopEntity> stops =
                scheduleStopService.getStopsForSchedule(scheduleId);

        return stops.stream()
                .map(s -> new ScheduleStopDto(
                        s.getStation().getId(),
                        s.getStation().getName(),
                        s.getStation().getGpsLat(),
                        s.getStation().getGpsLng(),
                        s.getStopSequence(),
                        s.getTime() != null
                                ? s.getTime().toString().substring(0, 5)
                                : null,
                        s.isNextDay()
                ))
                .toList();
    }

    private record ScoredSchedule(
            ScheduleEntity schedule,
            Double score
    ) {}

    private Double scoreSchedule(
            LocalDateTime now,
            LocalDate today,
            ScheduleEntity s,
            double progressMin
    ) {
        LocalDateTime dep = LocalDateTime.of(today, s.getDeparture());

        LocalDate arrDate =
                s.isArrivalNextDay() ? today.plusDays(1) : today;

        LocalDateTime arr =
                LocalDateTime.of(arrDate, s.getArrival());

        if (arr.isBefore(dep)) {
            arr = arr.plusDays(1);
        }

        long elapsed = Duration.between(dep, now).toMinutes();
        long duration = Duration.between(dep, arr).toMinutes();

        if (elapsed < -10 || elapsed > duration + 20) return null;

        return Math.abs(elapsed - progressMin);
    }
}
