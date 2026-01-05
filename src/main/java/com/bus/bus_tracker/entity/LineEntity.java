package com.bus.bus_tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore; // Import je bitan
import java.util.List;

@Getter
@Setter
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

    @JsonIgnore
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<BusEntity> buses;

    @JsonIgnore
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<RouteStationEntity> routeStations;

    @JsonIgnore
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<ScheduleEntity> schedules;
}