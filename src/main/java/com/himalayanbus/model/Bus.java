package com.himalayanbus.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer busId;

    private String busName;

    private String driverName;

    private String busType;

    private String routeFrom;

    private String routeTo;

    private LocalDate JourneyDate;

    private LocalTime arrivalTime;

    private LocalTime departureTime;

    private Integer totalSeats;

    private Integer availableSeats;

}