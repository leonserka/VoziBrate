package com.bus.bus_tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    @Column(name = "minutes_from_start", nullable = false)
    private Integer minutesFromStart = 0;

}
