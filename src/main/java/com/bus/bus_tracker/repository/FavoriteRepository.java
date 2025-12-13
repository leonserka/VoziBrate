package com.bus.bus_tracker.repository;

import com.bus.bus_tracker.entity.FavoriteEntity;
import com.bus.bus_tracker.entity.LineEntity;
import com.bus.bus_tracker.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<FavoriteEntity, Long> {

    List<FavoriteEntity> findByUser(UserEntity user);

    Optional<FavoriteEntity> findByUserAndLine(UserEntity user, LineEntity line);

    void deleteByUserAndLine(UserEntity user, LineEntity line);
}
