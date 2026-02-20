package com.rideDemo.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class IdempotencyRecord {
    @Id
    private String idempotencyKey;

    private String resourceId;
    private String type; // RI
}
