package com.bus.bus_tracker.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "lines")
public class LineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "line_number", nullable = false)
    private String lineNumber;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "direction")
    private String direction;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<BusEntity> buses;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<RouteStationEntity> routeStations;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<ScheduleEntity> schedules;
}
