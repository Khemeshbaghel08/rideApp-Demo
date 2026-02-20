package com.rideDemo.demo.service;

import com.rideDemo.demo.dto.TripSummary;
import com.rideDemo.demo.entity.Driver;
import com.rideDemo.demo.entity.Ride;
import com.rideDemo.demo.entity.Trip;
import com.rideDemo.demo.enums.DriverStatus;
import com.rideDemo.demo.enums.RideStatus;
import com.rideDemo.demo.enums.TripStatus;
import com.rideDemo.demo.repository.DriverRepository;
import com.rideDemo.demo.repository.RideRepository;
import com.rideDemo.demo.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class TripService {
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private DriverRepository driverRepository;

    @Transactional
    public void startTrip(String tripId) {

        Trip trip = tripRepository.findByIdForUpdate(tripId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Trip not found"));

        if (trip.getStatus() != TripStatus.CREATED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Invalid state transition");
        }

        Ride ride = rideRepository.findByIdForUpdate(trip.getRideId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Ride not found"));

        trip.setStatus(TripStatus.ONGOING);
        trip.setStartTime(LocalDateTime.now());

        ride.setStatus(RideStatus.ONGOING);
        ride.setStartedAt(LocalDateTime.now());
    }

    @Transactional
    public TripSummary endTrip(String tripId) {

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found"));

        if (trip.getStatus() != TripStatus.ONGOING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Trip not ongoing");
        }

        Ride ride = rideRepository.findById(trip.getRideId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found"));

        LocalDateTime endTime = LocalDateTime.now();

        long durationMinutes =
                Duration.between(trip.getStartTime(), endTime).toMinutes();

        double finalFare = ride.getDistance() * 12;

        trip.setStatus(TripStatus.COMPLETED);
        trip.setEndTime(endTime);
        trip.setFare(finalFare);

        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(endTime);
        ride.setDuration(durationMinutes);
        ride.setFinalFare(finalFare);

        Driver driver = driverRepository.findById(trip.getDriverId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));

        driver.setStatus(DriverStatus.AVAILABLE);
        driver.setCurrentRideId(null);
        driver.setTotalRides(driver.getTotalRides() + 1);

        tripRepository.save(trip);
        rideRepository.save(ride);
        driverRepository.save(driver);

        return new TripSummary(
                trip.getId(),
                finalFare,
                ride.getDistance(),
                durationMinutes,
                ride.getStatus().name()
        );
    }
}
