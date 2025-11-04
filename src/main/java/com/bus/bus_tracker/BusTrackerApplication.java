package com.bus.bus_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.bus.bus_tracker")
@EntityScan(basePackages = "com.bus.bus_tracker.entity")
@EnableJpaRepositories(basePackages = "com.bus.bus_tracker.repository")
public class BusTrackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BusTrackerApplication.class, args);
    }
}
