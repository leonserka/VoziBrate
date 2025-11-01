package com.bus.bus_tracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @Column(name = "purchase_time")
    private LocalDateTime purchaseTime;

    @Column
    private Double price;

    @Column
    private String status;


}
