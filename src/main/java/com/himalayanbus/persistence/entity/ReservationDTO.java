package com.himalayanbus.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {



    private String departureLocation;

    private String destination;

    private LocalDate journeyDate;

    private Integer bookedSeat;

}
