package com.rideDemo.demo.entity;

import com.rideDemo.demo.enums.TripStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Version;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "trips")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String rideId;
    @Column(nullable = false)
    private String riderId;
    @Column(nullable = false)
    private String driverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripStatus status;

    private double fare;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Version
    private Long version;
}
