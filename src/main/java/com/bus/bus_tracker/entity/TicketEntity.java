package com.bus.bus_tracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private LineEntity line;

    @Column(name = "purchase_time")
    private LocalDateTime purchaseTime;

    @Column(name = "price")
    private Double price;

    @Column(name = "status")
    private String status = "active";
}
