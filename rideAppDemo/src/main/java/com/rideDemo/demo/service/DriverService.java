package com.rideDemo.demo.service;

import com.rideDemo.demo.dto.DriverLocation;
import com.rideDemo.demo.entity.Driver;
import com.rideDemo.demo.entity.Location;
import com.rideDemo.demo.entity.Ride;
import com.rideDemo.demo.entity.Trip;
import com.rideDemo.demo.enums.DriverStatus;
import com.rideDemo.demo.enums.RideStatus;
import com.rideDemo.demo.enums.TripStatus;
import com.rideDemo.demo.repository.DriverRepository;
import com.rideDemo.demo.repository.RideRepository;
import com.rideDemo.demo.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class DriverService {
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private TripRepository tripRepository;

    @Transactional
    public Driver createDriver(Driver driver) {

        driver.setStatus(DriverStatus.AVAILABLE);
        driver.setCurrentRideId(null);
        driver.setCreatedAt(LocalDateTime.now());
        driver.setUpdatedAt(LocalDateTime.now());

        return driverRepository.save(driver);
    }

    @Transactional
    public void updateLocation(String driverId, DriverLocation request) {

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        if (driver.getCurrentLocation() == null) {
            driver.setCurrentLocation(new Location());
        }

        driver.getCurrentLocation().setLatitude(request.getLatitude());
        driver.getCurrentLocation().setLongitude(request.getLongitude());

        driver.setUpdatedAt(LocalDateTime.now());

        driverRepository.save(driver);
    }

    @Transactional
    public String acceptRide(String driverId, String rideId) {

        Driver driver = driverRepository.findByIdForUpdate(driverId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Driver not found"));

        Ride ride = rideRepository.findByIdForUpdate(rideId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ride not found"));

        if (ride.getStatus() != RideStatus.OFFERED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ride not in OFFERED state");
        }

        if (!ride.getDriverId().equals(driverId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ride not assigned to this driver");
        }

        ride.setStatus(RideStatus.ACCEPTED);
        driver.setStatus(DriverStatus.ON_TRIP);

        Trip trip = new Trip();
        trip.setRideId(rideId);
        trip.setDriverId(driverId);
        trip.setRiderId(ride.getRiderId());
        trip.setStatus(TripStatus.CREATED);

        tripRepository.save(trip);
        rideRepository.save(ride);
        driverRepository.save(driver);

        return trip.getId();
    }
}
