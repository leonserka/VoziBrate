package com.bus.bus_tracker.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "stations")
public class StationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="location")
    private String location;

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL)
    private List<RouteStationEntity> routeStations;

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL)
    private List<SalesPointEntity> salesPoints;
}
