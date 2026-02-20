package com.rideDemo.demo.dto;

import com.rideDemo.demo.entity.Location;
import com.rideDemo.demo.enums.RideStatus;
import lombok.Data;

@Data
public class RideResponse {
    private String rideId;

    private RideStatus status;

    private String driverId;
    private String driverName;
    private String driverPhone;

    private double estimatedFare;
    private double finalFare;

    private Location pickupLocation;
    private Location dropLocation;
}
