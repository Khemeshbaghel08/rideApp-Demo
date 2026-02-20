package com.rideDemo.demo.dto;

import com.rideDemo.demo.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private String paymentId;
    private String rideId;
    private double amount;
    private PaymentStatus status;
    private String pspReferenceId;
}
