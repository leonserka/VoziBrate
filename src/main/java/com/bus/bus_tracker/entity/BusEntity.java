package com.bus.bus_tracker.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "buses")
public class BusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bus_number")
    private String busNumber;

    @Column(name = "registration")
    private String registration;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private LineEntity line;

    @Column(name = "gps_id")
    private String gpsId;

    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL)
    private List<BusPositionEntity> positions;
}
