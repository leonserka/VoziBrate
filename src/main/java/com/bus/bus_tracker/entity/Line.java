package com.bus.bus_tracker.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "lines")
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "line_number", nullable = false)
    private String lineNumber;

    @Column(nullable = false)
    private String name;

    @Column
    private String direction;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Bus> buses;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Ticket> tickets;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Schedule> schedules;

}
