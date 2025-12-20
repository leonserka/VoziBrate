package com.bus.bus_tracker.specification;

import com.bus.bus_tracker.entity.LineEntity;
import org.springframework.data.jpa.domain.Specification;

public class LineSpecification {

    public static Specification<LineEntity> search(String q) {

        return (root, query, cb) -> {

            String like = "%" + q.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("lineNumber")), like),
                    cb.like(cb.lower(root.get("name")), like)
            );
        };
    }
}
