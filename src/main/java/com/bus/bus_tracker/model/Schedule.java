package com.bus.bus_tracker.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @Column(nullable = false)
    private LocalTime departure;

    @Column(nullable = false)
    private LocalTime arrival;

    @Column(name = "day_of_week")
    private String dayOfWeek;

    @Column
    private String direction;

}
