package com.rideDemo.demo.service;

import com.rideDemo.demo.dto.PaymentRequest;
import com.rideDemo.demo.dto.PaymentResponse;
import com.rideDemo.demo.entity.Payment;
import com.rideDemo.demo.entity.Ride;
import com.rideDemo.demo.enums.PaymentStatus;
import com.rideDemo.demo.enums.RideStatus;
import com.rideDemo.demo.repository.PaymentRepository;
import com.rideDemo.demo.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private RideRepository rideRepository;

    @Transactional
    public PaymentResponse processPayment(String idempotencyKey,
                                          PaymentRequest request) {

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Idempotency-Key header required");
        }


        Optional<Payment> existing =
                paymentRepository.findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {
            return mapToResponse(existing.get());
        }


        Ride ride = rideRepository.findByIdForUpdate(request.getRideId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Ride not found"));


        if (ride.getStatus() != RideStatus.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ride not completed yet");
        }

        boolean alreadyPaid =
                paymentRepository.existsByRideIdAndStatus(
                        ride.getId(), PaymentStatus.SUCCESS);

        if (alreadyPaid) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ride already paid");
        }


        Payment payment = new Payment();
        payment.setRideId(ride.getId());
        payment.setAmount(request.getAmount());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setIdempotencyKey(idempotencyKey);
        payment.setPspReferenceId("PSP-" + UUID.randomUUID());

        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        return mapToResponse(saved);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getId());
        response.setRideId(payment.getRideId());
        response.setAmount(payment.getAmount());
        response.setStatus(payment.getStatus());
        response.setPspReferenceId(payment.getPspReferenceId());
        return response;
    }
}
