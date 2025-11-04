package com.bus.bus_tracker.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "lines")
public class LineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<BusEntity> buses;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<RouteStationEntity> routeStations;
}
