package com.bus.bus_tracker.specification;

import com.bus.bus_tracker.entity.StationEntity;
import org.springframework.data.jpa.domain.Specification;

public class StationSpecification {

    public static Specification<StationEntity> search(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) {
                return cb.conjunction();
            }

            String like = "%" + q.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("location")), like)
            );
        };
    }
}
