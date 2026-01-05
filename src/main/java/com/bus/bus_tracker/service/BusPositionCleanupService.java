package com.bus.bus_tracker.service;

import com.bus.bus_tracker.repository.BusPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BusPositionCleanupService {

    private final BusPositionRepository positionRepository;

    @Scheduled(initialDelay = 10_000L, fixedRate = 2 * 60 * 60 * 1000L)
    public void cleanupOldPositions() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(2);
        int deleted = positionRepository.deleteOlderThan(cutoff);
        System.out.println("🧹 Cleanup: obrisano " + deleted + " pozicija starijih od 2h");
    }
}
