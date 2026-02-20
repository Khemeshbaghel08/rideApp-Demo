package com.rideDemo.demo.controller;

import com.rideDemo.demo.dto.AcceptRide;
import com.rideDemo.demo.dto.DriverLocation;
import com.rideDemo.demo.dto.RideRequest;
import com.rideDemo.demo.dto.RideResponse;
import com.rideDemo.demo.entity.Driver;
import com.rideDemo.demo.enums.DriverStatus;
import com.rideDemo.demo.repository.DriverRepository;
import com.rideDemo.demo.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/v1/drivers")
@RequiredArgsConstructor
public class DriverController {
    @Autowired
    private DriverService driverService;

    @PostMapping
    public ResponseEntity<Driver> createDriver(@Valid @RequestBody Driver driver) {
        Driver saved = driverService.createDriver(driver);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/{id}/location")
    public ResponseEntity<Void> updateLocation(
            @PathVariable String id,
            @Valid @RequestBody DriverLocation request
    ) {
        driverService.updateLocation(id, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Map<String, String>> acceptRide(
            @PathVariable String id,
            @RequestBody AcceptRide request
    ) {
        String tripId = driverService.acceptRide(id, request.getRideId());
        return ResponseEntity.ok(Map.of("tripId", tripId));
    }
}
