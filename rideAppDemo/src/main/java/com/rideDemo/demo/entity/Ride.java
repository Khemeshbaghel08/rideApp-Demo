package com.rideDemo.demo.entity;

import com.rideDemo.demo.enums.RideStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;

@Entity
@Table(name = "rides",
        indexes = {
                @Index(name = "idx_ride_rider", columnList = "rider_id"),
                @Index(name = "idx_ride_driver", columnList = "driver_id"),
                @Index(name = "idx_ride_status", columnList = "status")
        })
@Getter
@Setter
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "rider_id", nullable = false)
    private String riderId;

    @Column(name = "driver_id")
    private String driverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status;

    @Column(nullable = false)
    private double estimatedFare;
    private double finalFare;

    private double distance;
    private double duration;

    @Column(unique = true)
    private String idempotencyKey;

    @Column(nullable = false)
    private LocalDateTime requestedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime offerExpiresAt;

    @Version
    private Long version;
}
