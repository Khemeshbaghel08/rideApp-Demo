package com.rideDemo.demo.dto;

import com.rideDemo.demo.entity.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.service.annotation.GetExchange;

@Data
@Getter
@Setter
public class RideRequest {
    @NotBlank
    private String riderId;

    @NotNull
    private Location pickupLocation;

    @NotNull
    private Location dropLocation;

    @NotNull
    private Double distance;
}
