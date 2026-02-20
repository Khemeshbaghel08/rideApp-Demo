package com.rideDemo.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    private String id;

    private String username;
    private String password;
    private String email;

}
