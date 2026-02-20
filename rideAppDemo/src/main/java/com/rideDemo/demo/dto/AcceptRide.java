package com.rideDemo.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AcceptRide {
    @NotBlank
    private String rideId;
}
