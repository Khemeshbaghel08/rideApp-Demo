package com.rideDemo.demo.controller;

import com.rideDemo.demo.dto.TripSummary;
import com.rideDemo.demo.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/trips")
@RequiredArgsConstructor
public class TripController {

    @Autowired
    private TripService tripService;

    @PostMapping("/{id}/start")
    public ResponseEntity<Void> startTrip(@PathVariable String id) {
        tripService.startTrip(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/end")
    public ResponseEntity<TripSummary> endTrip(@PathVariable String id) {
        TripSummary response = tripService.endTrip(id);
        return ResponseEntity.ok(response);
    }
}
