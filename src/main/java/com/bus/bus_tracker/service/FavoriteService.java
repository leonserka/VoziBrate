package com.bus.bus_tracker.service;

import com.bus.bus_tracker.entity.FavoriteEntity;
import com.bus.bus_tracker.entity.LineEntity;
import com.bus.bus_tracker.entity.UserEntity;
import com.bus.bus_tracker.repository.FavoriteRepository;
import com.bus.bus_tracker.repository.LineRepository;
import com.bus.bus_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final LineRepository lineRepository;

    private UserEntity getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<FavoriteEntity> getUserFavorites(String email) {
        return favoriteRepository.findByUser(getCurrentUser(email));
    }

    public boolean isFavorite(String email, Long lineId) {
        UserEntity user = getCurrentUser(email);
        LineEntity line = lineRepository.findById(lineId)
                .orElseThrow(() -> new RuntimeException("Line not found"));

        return favoriteRepository.findByUserAndLine(user, line).isPresent();
    }

    public void toggleFavorite(String email, Long lineId) {
        UserEntity user = getCurrentUser(email);
        LineEntity line = lineRepository.findById(lineId)
                .orElseThrow(() -> new RuntimeException("Line not found"));

        favoriteRepository.findByUserAndLine(user, line)
                .ifPresentOrElse(
                        favoriteRepository::delete,
                        () -> {
                            FavoriteEntity f = new FavoriteEntity();
                            f.setUser(user);
                            f.setLine(line);
                            favoriteRepository.save(f);
                        }
                );
    }

    public void removeFavorite(String email, Long lineId) {
        UserEntity user = getCurrentUser(email);
        LineEntity line = lineRepository.findById(lineId)
                .orElseThrow(() -> new RuntimeException("Line not found"));

        favoriteRepository.deleteByUserAndLine(user, line);
    }
}
