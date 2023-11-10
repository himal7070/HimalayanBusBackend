package com.himalayanbus.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer reservationID;

    private String status;

    private LocalDate date;

    private LocalTime time;

    private String departureLocation;

    private String destination;

    private LocalDate journeyDate;

    private Integer bookedSeat;

    private Integer fare;

    @ManyToOne
    private Bus bus;

    @ManyToOne
    private User user;

}
