package com.rideDemo.demo.repository;

import com.rideDemo.demo.entity.Payment;
import com.rideDemo.demo.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    boolean existsByRideIdAndStatus(String rideId, PaymentStatus status);
}
