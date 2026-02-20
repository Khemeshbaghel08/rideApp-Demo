package com.rideDemo.demo.controller;

import com.rideDemo.demo.dto.RideRequest;
import com.rideDemo.demo.dto.RideResponse;
import com.rideDemo.demo.service.RideService;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/rides")
@RequiredArgsConstructor
public class RideController {
    @Autowired
    private RideService rideService;

    @PostMapping
    public ResponseEntity<RideResponse> requestRide(
            @Valid @RequestBody RideRequest request,
            @RequestHeader(value = "Idempotency-Key") String idempotencyKey
    ) {
        RideResponse response = rideService.createRide(request, idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    @GetMapping("/{id}")
    public ResponseEntity<RideResponse> getRideStatus(@PathVariable String id) {
        RideResponse response = rideService.getRideStatus(id);
        return ResponseEntity.ok(response);
    }
}
