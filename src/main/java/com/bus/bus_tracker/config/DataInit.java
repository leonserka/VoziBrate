package com.bus.bus_tracker.config;

import com.bus.bus_tracker.service.ScheduleStopService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInit {

    private final ScheduleStopService scheduleStopService;

    @Bean
    ApplicationRunner initScheduleStops() {
        return args -> {
            scheduleStopService.regenerateStopsForAllSchedules();
        };
    }
}
