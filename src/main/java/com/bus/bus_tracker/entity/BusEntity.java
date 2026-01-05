package com.bus.bus_tracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore; // <--- 1. IMPORT JE BITAN!
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
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

    @Column(name = "gps_id")
    private String gpsId;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private LineEntity line;

    @JsonIgnore
    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL)
    private List<BusPositionEntity> positions;
}