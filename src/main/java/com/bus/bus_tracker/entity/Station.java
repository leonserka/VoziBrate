package com.bus.bus_tracker.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "stations")
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String location;

    @Column(name = "gps_lat")
    private Double gpsLat;

    @Column(name = "gps_lng")
    private Double gpsLng;

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL)
    private List<RouteStation> routeStations;

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL)
    private List<Favorite> favorites;

}
