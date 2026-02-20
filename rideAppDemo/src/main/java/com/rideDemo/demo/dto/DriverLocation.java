package com.rideDemo.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DriverLocation {
    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;
}
