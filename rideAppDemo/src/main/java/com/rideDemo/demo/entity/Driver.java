package com.rideDemo.demo.entity;

import com.rideDemo.demo.enums.DriverStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;

@Entity
@Table(name = "drivers",
        indexes = {
                @Index(name = "idx_driver_status", columnList = "status"),
                @Index(name = "idx_driver_user", columnList = "user_id", unique = true)
        })
@Getter
@Setter
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverStatus status;

    @Embedded
    private Vehicle vehicle;

    @Embedded
    private Location currentLocation;

    private double rating;
    private int totalRides;

    @Column(name = "current_ride_id", unique = true)
    private String currentRideId;

    private boolean verified;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @Version
    private Long version;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
