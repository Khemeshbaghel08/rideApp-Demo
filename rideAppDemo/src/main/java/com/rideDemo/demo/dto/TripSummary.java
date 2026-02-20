package com.rideDemo.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripSummary {
    private String tripId;
    private double fare;
    private double distance;
    private double duration;
    private String status;
}
