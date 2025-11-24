package com.bus.bus_tracker.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "route_stations")
public class RouteStationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private LineEntity line;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private StationEntity station;

    @Column(name = "order_number")
    private Integer orderNumber;
}
