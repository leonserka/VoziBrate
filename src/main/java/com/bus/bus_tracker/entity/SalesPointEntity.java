package com.bus.bus_tracker.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sales_points")
public class SalesPointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address")
    private String address;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private StationEntity station;
}
