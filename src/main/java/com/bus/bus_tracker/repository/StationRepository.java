package com.bus.bus_tracker.repository;

import com.bus.bus_tracker.entity.StationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository

public interface StationRepository
        extends JpaRepository<StationEntity, Long>,
        JpaSpecificationExecutor<StationEntity> {
}
