package com.bus.bus_tracker.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sales_points")
public class SalesPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String address;

    @Column(name = "gps_lat")
    private Double gpsLat;

    @Column(name = "gps_lng")
    private Double gpsLng;

}
