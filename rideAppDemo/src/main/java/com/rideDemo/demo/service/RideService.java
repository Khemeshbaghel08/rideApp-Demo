package com.rideDemo.demo.service;

import com.rideDemo.demo.dto.RideRequest;
import com.rideDemo.demo.dto.RideResponse;
import com.rideDemo.demo.entity.Driver;
import com.rideDemo.demo.entity.Ride;
import com.rideDemo.demo.enums.DriverStatus;
import com.rideDemo.demo.enums.RideStatus;
import com.rideDemo.demo.repository.DriverRepository;
import com.rideDemo.demo.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RideService {
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private DriverRepository driverRepository;

    @Transactional
    public RideResponse createRide(RideRequest request, String idempotencyKey) {

        Optional<Ride> existing =
                rideRepository.findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {
            return mapToResponse(existing.get());
        }

        Ride ride = new Ride();
        ride.setRiderId(request.getRiderId());
        ride.setDistance(request.getDistance());
        ride.setEstimatedFare(request.getDistance() * 10);
        ride.setStatus(RideStatus.REQUESTED);
        ride.setRequestedAt(LocalDateTime.now());
        ride.setIdempotencyKey(idempotencyKey);

        rideRepository.save(ride);


        List<Driver> drivers =
                driverRepository.findAvailableDriversForUpdate(
                        PageRequest.of(0, 1));

        if (!drivers.isEmpty()) {

            Driver driver = drivers.get(0);

            driver.setStatus(DriverStatus.RESERVED);
            driver.setCurrentRideId(ride.getId());

            ride.setDriverId(driver.getId());
            ride.setStatus(RideStatus.OFFERED);

            driverRepository.save(driver);
            rideRepository.save(ride);
        }

        return mapToResponse(ride);
    }

    public RideResponse getRideStatus(String rideId) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        return mapToResponse(ride);
    }

    @Transactional
    public void startRide(String rideId) {

        Ride ride = rideRepository.findByIdForUpdate(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new RuntimeException("Ride must be ACCEPTED before starting");
        }

        boolean driverHasActiveRide =
                rideRepository.existsByDriverIdAndStatusIn(
                        ride.getDriverId(),
                        List.of(RideStatus.STARTED)
                );

        if (driverHasActiveRide) {
            throw new RuntimeException("Driver already has an active ride");
        }

        ride.setStatus(RideStatus.STARTED);
        rideRepository.save(ride);
    }

    @Transactional
    public void endRide(String rideId) {

        Ride ride = rideRepository.findByIdForUpdate(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (ride.getStatus() != RideStatus.STARTED) {
            throw new RuntimeException("Ride is not in STARTED state");
        }

        ride.setStatus(RideStatus.COMPLETED);
        rideRepository.save(ride);
    }

    private RideResponse mapToResponse(Ride ride) {
        RideResponse response = new RideResponse();
        response.setRideId(ride.getId());
        response.setStatus(ride.getStatus());
        response.setEstimatedFare(ride.getEstimatedFare());
        response.setFinalFare(ride.getFinalFare());
        response.setDriverId(ride.getDriverId());
        return response;
    }
}
