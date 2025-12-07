package com.bus.bus_tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "schedules")
public class ScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private LineEntity line;

    @Column(name = "departure")
    private LocalTime departure;

    @Column(name = "arrival")
    private LocalTime arrival;

    @Column(name = "day_of_week")
    private String dayOfWeek;

    @Column(name = "direction")
    private String direction;
}
