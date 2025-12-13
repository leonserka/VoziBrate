package com.bus.bus_tracker.repository;

import com.bus.bus_tracker.entity.LineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface LineRepository extends JpaRepository<LineEntity, Long> {

    List<LineEntity> findByLineNumberContainingIgnoreCaseOrNameContainingIgnoreCase(
            String lineNumber,
            String name
    );
}
