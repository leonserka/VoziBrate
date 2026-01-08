package com.bus.bus_tracker.repository;

import com.bus.bus_tracker.entity.LineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LineRepository
        extends JpaRepository<LineEntity, Long>,
        JpaSpecificationExecutor<LineEntity> {

    List<LineEntity> findAllByLineNumber(String lineNumber);

    Optional<LineEntity> findByLineNumberAndVariant(String lineNumber, String variant);
}
