package com.rideDemo.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotBlank
    private String rideId;

    @NotNull
    private Double amount;

}
