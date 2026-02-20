package com.rideDemo.demo.controller;

import com.rideDemo.demo.dto.PaymentRequest;
import com.rideDemo.demo.dto.PaymentResponse;
import com.rideDemo.demo.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody PaymentRequest request) {

        PaymentResponse response =
                paymentService.processPayment(idempotencyKey, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
