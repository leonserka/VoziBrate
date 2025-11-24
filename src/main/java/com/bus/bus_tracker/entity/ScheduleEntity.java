package com.bus.bus_tracker.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "schedules")
public class ScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private LineEntity line;

    @Column(name = "departure", nullable = false)
    private LocalTime departure;

    @Column(name = "arrival", nullable = false)
    private LocalTime arrival;

    @Column(name = "day_of_week")
    private String dayOfWeek;

    @Column(name = "direction")
    private String direction;
}
