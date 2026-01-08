package com.bus.bus_tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "schedule_stops", uniqueConstraints = @UniqueConstraint(columnNames = {"schedule_id", "stop_sequence"}))
public class ScheduleStopEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "schedule_id")
    private ScheduleEntity schedule;

    @ManyToOne(optional = false)
    @JoinColumn(name = "station_id")
    private StationEntity station;

    @Column(name = "stop_sequence", nullable = false)
    private Integer stopSequence;

    @Column(name = "time", nullable = false)
    private LocalTime time;

    @Column(name = "next_day", nullable = false)
    private boolean nextDay;

}
