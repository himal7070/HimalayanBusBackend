package com.himalayanbus.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
    private Integer fare;

    @ManyToOne(cascade = CascadeType.ALL)
    private Route route;


}