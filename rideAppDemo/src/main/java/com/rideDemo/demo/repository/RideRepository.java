package com.rideDemo.demo.repository;

import com.rideDemo.demo.entity.Ride;
import com.rideDemo.demo.enums.RideStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, String> {
    Optional<Ride> findByIdempotencyKey(String idempotencyKey);

    boolean existsByDriverIdAndStatusIn(String driverId,
                                        Collection<RideStatus> statuses);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Ride r WHERE r.id = :id")
    Optional<Ride> findByIdForUpdate(@Param("id") String id);
}
